package com.example.ledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

// import com.example.ledger.repository.UserRepository;
// import com.example.ledger.service.UserService;

@Controller
@RequestMapping("/mypage")
public class GeneralUserController {

  // private final UserRepository userRepository;
  // private final UserService userService;

  // public GeneralUserController(
  //   UserRepository userRepository,
  //   UserService userService
  // ) {
  //   this.userRepository = userRepository;
  //   this.userService = userService;
  // }
  
  @GetMapping
  public String index() {
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

    return "redirect:/mypage";
  }
}
