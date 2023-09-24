package com.example.ledger.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.Tag;
import com.example.ledger.entity.TagPayment;
import com.example.ledger.entity.User;
import com.example.ledger.form.TagRegisterForm;
import com.example.ledger.repository.TagPaymentRepository;
import com.example.ledger.repository.TagRepository;
import com.example.ledger.security.UserDetailsImpl;
import com.example.ledger.service.TagService;

@Controller
@RequestMapping("/tags")
public class TagController {
  
  private final TagRepository tagRepository;
  private final TagService tagService;
  private final TagPaymentRepository tagPaymentRepository;
  private final PaymentController paymentController;

  public TagController(
    TagRepository tagRepository,
    TagService tagService,
    TagPaymentRepository tagPaymentRepository,
    PaymentController paymentController
  ) {
    this.tagRepository = tagRepository;
    this.tagService = tagService;
    this.tagPaymentRepository = tagPaymentRepository;
    this.paymentController = paymentController;
  }

  @GetMapping
  public String index(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Tag> tags = this.tagRepository.findByUserOrderByCreatedAtDesc(user);
    TagRegisterForm tagRegisterForm = new TagRegisterForm(user.getId(), null);

    model.addAttribute("tags", tags);
    model.addAttribute("tagRegisterForm", tagRegisterForm);

    return "tags/index";
  }

  @GetMapping("/{id}")
  public String show(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Tag> tags = this.tagRepository.findByUserOrderByCreatedAtDesc(user);
    Tag tag = this.tagRepository.getReferenceById(id);
    if(tag.getUser().getId() != user.getId()) {
      return "redirect:/tags";
    }

    HashMap<String, String> methodIcons = this.paymentController.methodIcons();

    Page<TagPayment> lists = this.tagPaymentRepository.findByTag(tag, pageable);
    model.addAttribute("lists", lists);
    model.addAttribute("tags", tags);
    model.addAttribute("tag", tag);
    model.addAttribute("methodIcons", methodIcons);

    return "tags/show";
  }

  @GetMapping("/{id}/edit")
  public String edit(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    Tag tag = this.tagRepository.getReferenceById(id);
    if(tag.getUser().getId() != user.getId()) {
      return "redirect:/tags/{id}";
    }

    TagRegisterForm tagRegisterForm = new TagRegisterForm(user.getId(), tag.getName());
    model.addAttribute("tag", tag);
    model.addAttribute("tagRegisterForm", tagRegisterForm);

    return "tags/edit";
  }

  @PostMapping("/create")
  public String create(
    @ModelAttribute @Validated TagRegisterForm tagRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    if(this.tagService.existsName(user, tagRegisterForm.getName())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "そのタグ名は既に存在します");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      List<Tag> tags = this.tagRepository.findByUserOrderByCreatedAtDesc(user);
      model.addAttribute("tags", tags);
      model.addAttribute("errorMsg", "入力内容に誤りがあります");

      return "tags/index";
    }

    this.tagService.create(tagRegisterForm);
    redirectAttributes.addFlashAttribute("successMsg", "タグを追加しました");

    return "redirect:/tags";
  }

  @PostMapping("/{id}/update")
  public String update(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated TagRegisterForm tagRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    if(this.tagService.existsName(user, tagRegisterForm.getName())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "そのタグ名は既に存在します");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      Tag tag = this.tagRepository.getReferenceById(id);
      model.addAttribute("tag", tag);
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      return "tags/edit";
    }

    this.tagService.update(tagRegisterForm, id);
    redirectAttributes.addFlashAttribute("successMsg", "タグ名を更新しました");

    return "redirect:/tags/{id}";
  }

  @PostMapping("/{id}/delete")
  public String delete(
    @PathVariable(name = "id") Integer id,
    RedirectAttributes redirectAttributes
  ) {
    this.tagRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("successMsg", "タグを削除しました");

    return "redirect:/tags";
  }

}
