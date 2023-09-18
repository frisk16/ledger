package com.example.ledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ledger.entity.Tag;
import com.example.ledger.entity.User;
import com.example.ledger.repository.TagRepository;
import com.example.ledger.security.UserDetailsImpl;

@Controller
@RequestMapping("/tags")
public class TagController {
  
  private final TagRepository tagRepository;

  public TagController(
    TagRepository tagRepository
  ) {
    this.tagRepository = tagRepository;
  }

  @GetMapping
  public String index(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Tag> tags = this.tagRepository.findByUserOrderByCreatedAtDesc(user);

    model.addAttribute("tags", tags);

    return "tags/index";
  }

}
