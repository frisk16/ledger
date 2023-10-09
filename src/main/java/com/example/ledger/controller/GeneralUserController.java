package com.example.ledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.User;
import com.example.ledger.form.UserEditForm;
import com.example.ledger.form.UserEditPasswordForm;
import com.example.ledger.security.UserDetailsImpl;

import com.example.ledger.service.UserService;

@Controller
@RequestMapping("/mypage")
public class GeneralUserController {

  private final UserService userService;

  public GeneralUserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    User user = userDetailsImpl.getUser();

    model.addAttribute("user", user);

    return "users/general/index";
  }
  
  @GetMapping("/edit")
  public String edit(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    UserEditForm userEditForm = new UserEditForm(
      user.getName(),
      user.getEmail(),
      user.getAddress(),
      user.getPhoneNumber()
    );

    model.addAttribute("userEditForm", userEditForm);

    return "users/general/edit";
  }

  @PostMapping("/update")
  public String update(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @ModelAttribute @Validated UserEditForm userEditForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    User user = userDetailsImpl.getUser();
    String newName = userEditForm.getName();
    String newEmail = userEditForm.getEmail();

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

      return "users/general/edit";
    }

    this.userService.update(userEditForm, user);
    redirectAttributes.addFlashAttribute("warningMsg", "ユーザー情報が変更されました、再度ログイン後に反映されます");

    return "redirect:/mypage";
  }

  @GetMapping("/editPassword")
  public String editPassword(Model model) {

    model.addAttribute("userEditPasswordForm", new UserEditPasswordForm());
    
    return "users/general/editPassword";
  }

  @PostMapping("/updatePassword")
  public String updatePassword(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @ModelAttribute @Validated UserEditPasswordForm userEditPasswordForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    User user = userDetailsImpl.getUser();
    String currentPassword = userEditPasswordForm.getCurrentPassword();
    String newPassword = userEditPasswordForm.getNewPassword();
    String passwordConfirmation = userEditPasswordForm.getPasswordConfirmation();

    if(!this.userService.verifyPassword(currentPassword, user)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "currentPassword", "現在のパスワードが一致しません");
      bindingResult.addError(fieldError);
    }
    if(!this.userService.samePasswordConfirmation(newPassword, passwordConfirmation)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "newPassword", "確認用パスワードと一致しません");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      model.addAttribute("errorMsg", "入力内容に誤りがあります");

      return "users/general/editPassword";
    }

    this.userService.updatePassword(userEditPasswordForm, user.getId());
    redirectAttributes.addFlashAttribute("successMsg", "パスワードが変更されました");

    return "redirect:/mypage";
  }
}
