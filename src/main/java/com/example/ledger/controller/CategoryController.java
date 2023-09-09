package com.example.ledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.User;
import com.example.ledger.form.CategoryAddForm;
import com.example.ledger.form.CategoryEditForm;
import com.example.ledger.repository.CategoryRepository;
import com.example.ledger.security.UserDetailsImpl;
import com.example.ledger.service.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryController {
  
  private final CategoryRepository categoryRepository;
  private final CategoryService categoryService;

  public CategoryController(
    CategoryRepository categoryRepository,
    CategoryService categoryService
  ) {
    this.categoryRepository = categoryRepository;
    this.categoryService = categoryService;
  }

  @GetMapping
  public String index(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Category> categories = this.categoryRepository.findByUser(user);
    CategoryAddForm categoryAddForm = new CategoryAddForm(user.getId(), null);
    CategoryEditForm categoryEditForm = new CategoryEditForm(null, user.getId(), null);

    model.addAttribute("categories", categories);
    model.addAttribute("categoryAddForm", categoryAddForm);
    model.addAttribute("categoryEditForm", categoryEditForm);

    return "categories/index";
  }

  @PostMapping("/add")
  public String add(
    @ModelAttribute CategoryAddForm categoryAddForm,
    RedirectAttributes redirectAttributes
    ) {      
      if(this.categoryService.isNameEmpty(categoryAddForm.getName())) {
        redirectAttributes.addFlashAttribute("errorMsg", "カテゴリー名を入力してください");
        return "redirect:/categories";
      }
      if(this.categoryService.isNameOfOverLength(categoryAddForm.getName())) {
        redirectAttributes.addFlashAttribute("errorMsg", "20文字以内で設定してください");
        return "redirect:/categories";
      }
      if(this.categoryService.existsName(categoryAddForm.getUserId(), categoryAddForm.getName())) {
        redirectAttributes.addFlashAttribute("errorMsg", "そのカテゴリー名は既に存在します");
        return "redirect:/categories";
      }

      this.categoryService.create(categoryAddForm);
      redirectAttributes.addFlashAttribute("successMsg", "カテゴリーを追加しました");

      return "redirect:/categories";
  }

  @PostMapping("/{id}/update")
  public String update(
    @ModelAttribute CategoryEditForm categoryEditForm,
    RedirectAttributes redirectAttributes
  ) {
    if(this.categoryService.isNameEmpty(categoryEditForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "カテゴリー名を入力してください");
      return "redirect:/categories";
    }
    if(this.categoryService.isNameOfOverLength(categoryEditForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "20文字以内で設定してください");
      return "redirect:/categories";
    }
    if(this.categoryService.existsName(categoryEditForm.getUserId(), categoryEditForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "そのカテゴリー名は既に存在します");
      return "redirect:/categories";
    }

    this.categoryService.update(categoryEditForm);
    redirectAttributes.addFlashAttribute("successMsg", "カテゴリーを更新しました");

    return "redirect:/categories";
  }

  @PostMapping("/{id}/delete")
  public String delete(
    @PathVariable(name = "id") Integer id,
    RedirectAttributes redirectAttributes
  ) {
      this.categoryRepository.deleteById(id);
      redirectAttributes.addFlashAttribute("successMsg", "カテゴリーを削除しました");

      return "redirect:/categories";
  }
}
