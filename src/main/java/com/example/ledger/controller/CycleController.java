package com.example.ledger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ledger.entity.PartsCategory;
import com.example.ledger.repository.PartsCategoryRepository;

@Controller
@RequestMapping("/cycles")
public class CycleController {

  @Value("${aws.s3.image-folder-url}")
  private String imageUrl;

  private final PartsCategoryRepository categoryRepository;

  public CycleController(
    PartsCategoryRepository categoryRepository
  ) {
    this.categoryRepository = categoryRepository;
  }


  @GetMapping
  public String index(Model model) {
    List<PartsCategory> categories = this.categoryRepository.findAll();

    model.addAttribute("categories", categories);
    model.addAttribute("imageUrl", this.imageUrl);

    return "cycles/index";
  }

}
