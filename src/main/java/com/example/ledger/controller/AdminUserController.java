package com.example.ledger.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

import com.example.ledger.entity.User;
import com.example.ledger.form.AdminMemberEditForm;
import com.example.ledger.repository.UserRepository;
import com.example.ledger.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

  private final UserRepository userRepository;
  private final UserService userService;

  public AdminUserController(
    UserRepository userRepository,
    UserService userService
  ) {
    this.userRepository = userRepository;
    this.userService = userService;
  }
  
  @GetMapping
  public String index() {

    return "users/admin/index";
  }

  @GetMapping("/members")
  public String membersIndex(
    @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
    Model model
  ) {
    Page<User> users = this.userRepository.findAll(pageable);

    model.addAttribute("users", users);

    return "users/admin/members/index";
  }

  @GetMapping("/members/{id}")
  public String membersShow(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);

    model.addAttribute("user", user);

    return "users/admin/members/show";
  }

  @GetMapping("/members/{id}/edit")
  public String membersEdit(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);

    AdminMemberEditForm adminMemberEditForm = new AdminMemberEditForm(
      user.getName(),
      user.getEmail(),
      user.getAddress(),
      user.getPhoneNumber(),
      user.getPassword(),
      user.getEnabled()
    );

    model.addAttribute("user", user);
    model.addAttribute("adminMemberEditForm", adminMemberEditForm);

    return "users/admin/members/edit";
  }

  @PostMapping("/members/{id}/update")
  public String membersUpdate(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated AdminMemberEditForm adminMemberEditForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);
    String newName = adminMemberEditForm.getName();
    String newEmail = adminMemberEditForm.getEmail();

    if(this.userService.existsName(user.getName(), newName)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "そのユーザー名は既に使用されています");
      bindingResult.addError(fieldError);
    }
    if(this.userService.existsEmail(user.getEmail(), newEmail)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に使用されています");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      model.addAttribute("user", user);

      return "users/admin/members/edit";
    }

    this.userService.membersUpdate(adminMemberEditForm, user);
    redirectAttributes.addFlashAttribute("successMsg", "会員情報を更新しました");

    return "redirect:/admin/members/{id}";
  }

}
