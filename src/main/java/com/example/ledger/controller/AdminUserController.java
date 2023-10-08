package com.example.ledger.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ledger.entity.User;
import com.example.ledger.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

  private final UserRepository userRepository;

  public AdminUserController(
    UserRepository userRepository
  ) {
    this.userRepository = userRepository;
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

}
