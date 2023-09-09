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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.User;
import com.example.ledger.entity.VerificationToken;
import com.example.ledger.event.SignupEventPublisher;
import com.example.ledger.form.ResetPasswordForm;
import com.example.ledger.form.ResetPasswordMailForm;
import com.example.ledger.form.UserRegisterForm;
import com.example.ledger.repository.UserRepository;
import com.example.ledger.security.UserDetailsImpl;
import com.example.ledger.service.UserService;
import com.example.ledger.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final SignupEventPublisher signupEventPublisher;
  private final VerificationTokenService verificationTokenService;
  
  public AuthController(
    UserService userService,
    UserRepository userRepository,
    SignupEventPublisher signupEventPublisher,
    VerificationTokenService verificationTokenService
  ) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.signupEventPublisher = signupEventPublisher;
    this.verificationTokenService = verificationTokenService;
  }

  @GetMapping("/login")
  public String login(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    if(userDetailsImpl != null) {
      return "redirect:/";
    }
    
    return "auth/login";
  }

  @GetMapping("/register")
  public String register(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    if(userDetailsImpl != null) {
      return "redirect:/";
    }

    UserRegisterForm userRegisterForm = new UserRegisterForm();

    model.addAttribute("userRegisterForm", userRegisterForm);

    return "auth/register";
  }

  @PostMapping("/register")
  public String create(
    @ModelAttribute @Validated UserRegisterForm userRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    HttpServletRequest httpServletRequest,
    Model model
  ) {

    if(this.userService.isEmailRegistered(userRegisterForm.getEmail())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に存在します");
      bindingResult.addError(fieldError);
    }

    if(!this.userService.isSamePassword(userRegisterForm.getPassword(), userRegisterForm.getPasswordConfirmation())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "確認用パスワードが一致しません");
      bindingResult.addError(fieldError);
    }

    if(bindingResult.hasErrors()) {
      model.addAttribute("errorMessage", "入力内容に誤りがあります");
      return "auth/register";
    }

    User createdUser = this.userService.create(userRegisterForm);
    String requestUrl = new String(httpServletRequest.getRequestURL());
    this.signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
    redirectAttributes.addFlashAttribute("successMessage", "ご入力頂いたEメールアドレス宛に認証メールを送信しました。メールに記載されているリンクへアクセスし、登録を完了させてください。");
  
    return "redirect:/";
  }

  @GetMapping("/resetPassword")
  public String sendResetPasswordMailPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    if(userDetailsImpl != null) {
      return "redirect:/";
    }

    model.addAttribute("resetPasswordMailForm", new ResetPasswordMailForm());

    return "auth/sendResetPasswordMail";
  }

  @PostMapping("/resetPassword")
  public String sendResetPasswordMail(
    @ModelAttribute @Validated ResetPasswordMailForm resetPasswordMailForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    HttpServletRequest httpServletRequest,
    Model model
  ) {

    String email = resetPasswordMailForm.getEmail();

    if(!this.userService.isEmailRegistered(email)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは登録されていません");
      bindingResult.addError(fieldError);
    }

    if(bindingResult.hasErrors()) {
      model.addAttribute("errorMessage", "入力内容に誤りがあります");
      return "auth/sendResetPasswordMail";
    }

    User user = this.userRepository.findByEmail(email);
    String requestUrl = new String(httpServletRequest.getRequestURL());
    this.signupEventPublisher.publishSignupEvent(user, requestUrl);
    redirectAttributes.addFlashAttribute("successMessage", "入力頂いたEメールアドレス宛にパスワード再設定メールを送信しました、ご確認ください");

    return "redirect:/";
  }

  @GetMapping("/resetPassword/setting")
  public String resetPasswordPage(
    @RequestParam(name = "token", required = false) String token,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    if(userDetailsImpl != null) {
      return "redirect:/";
    }

    VerificationToken verificationToken = this.verificationTokenService.getVerificationToken(token);
      
    if(verificationToken != null) {
      Integer userId = verificationToken.getUser().getId();
      ResetPasswordForm resetPasswordForm = new ResetPasswordForm(token, userId, null, null);
      model.addAttribute("resetPasswordForm", resetPasswordForm);

      return "auth/resetPassword";
    } else {
      model.addAttribute("errorMessage", "ページが無効です");

      return "auth/verify";
    }
  }

  @PostMapping("/resetPassword/setting")
  public String resetPassword(
    @ModelAttribute @Validated ResetPasswordForm resetPasswordForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    Integer passwordLength = (int) resetPasswordForm.getPassword().length();
    if(passwordLength < 8) {
      redirectAttributes.addFlashAttribute("errorMessage", "最低8文字以上で設定してください");
    }

    if(!this.userService.isSamePassword(resetPasswordForm.getPassword(), resetPasswordForm.getPasswordConfirmation())) {
      redirectAttributes.addFlashAttribute("errorMessage", "確認用パスワードが一致しません");
    }

    if(bindingResult.hasErrors()) {
      return "redirect:/resetPassword/setting?token=" + resetPasswordForm.getToken();
    }

    this.userService.editPassword(resetPasswordForm);

    redirectAttributes.addFlashAttribute("successMessage", "パスワードの再設定が完了しました");

    return "redirect:/register/verify?token=" + resetPasswordForm.getToken();
  }

  @GetMapping("/register/verify")
  public String verify(
    @RequestParam(name = "token", required = false) String token,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    if(userDetailsImpl != null) {
      return "redirect:/";
    }

    VerificationToken verificationToken = this.verificationTokenService.getVerificationToken(token);

    if(verificationToken != null) {
      User user = verificationToken.getUser();
      this.userService.enableUser(user);
      model.addAttribute("successMessage", "設定が完了しました、ログインしてご確認ください");
    } else {
      model.addAttribute("errorMessage", "ページが無効です");
    }

    return "auth/verify";
  }

}
