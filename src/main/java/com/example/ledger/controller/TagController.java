package com.example.ledger.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ledger.entity.Payment;
import com.example.ledger.entity.Tag;
import com.example.ledger.entity.User;
import com.example.ledger.repository.TagRepository;
import com.example.ledger.security.UserDetailsImpl;

@Controller
@RequestMapping("/tags")
public class TagController {
  
  private final TagRepository tagRepository;
  private final PaymentController paymentController;

  public TagController(
    TagRepository tagRepository,
    PaymentController paymentController
  ) {
    this.tagRepository = tagRepository;
    this.paymentController = paymentController;
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

  @GetMapping("/{id}")
  public String show(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Tag> tags = this.tagRepository.findByUserOrderByCreatedAtDesc(user);
    Tag tag = this.tagRepository.getReferenceById(id);
    if(tag.getUser().getId() != user.getId()) {
      return "redirect:/tags";
    }

    HashMap<String, String> methodIcons = this.paymentController.methodIcons();

    List<Payment> payments = tag.getPayments();
    model.addAttribute("payments", payments);
    model.addAttribute("tags", tags);
    model.addAttribute("tag", tag);
    model.addAttribute("methodIcons", methodIcons);

    return "tags/show";
  }

}
