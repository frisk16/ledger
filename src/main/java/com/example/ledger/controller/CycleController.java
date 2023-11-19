package com.example.ledger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ledger.entity.Parts;
import com.example.ledger.entity.PartsCategory;
import com.example.ledger.repository.PartsCategoryRepository;
import com.example.ledger.repository.PartsRepository;

@Controller
@RequestMapping("/cycles")
public class CycleController {

  @Value("${aws.s3.image-folder-url}")
  private String imageUrl;

  private final PartsCategoryRepository categoryRepository;
  private final PartsRepository partsRepository;

  public CycleController(
    PartsCategoryRepository categoryRepository,
    PartsRepository partsRepository
  ) {
    this.categoryRepository = categoryRepository;
    this.partsRepository = partsRepository;
  }


  @GetMapping
  public String index(Model model) {
    List<PartsCategory> categories = this.categoryRepository.findAll();

    model.addAttribute("categories", categories);
    model.addAttribute("imageUrl", this.imageUrl);

    return "cycles/index";
  }

  @GetMapping("/{id}")
  public String show(
    @PathVariable(name = "id") Integer id,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    Model model
  ) {
    PartsCategory category = this.categoryRepository.getReferenceById(id);
    Page<Parts> parts = this.partsRepository.findByPartsCategoryOrderByCreatedAtDesc(category, pageable);

    model.addAttribute("category", category);
    model.addAttribute("parts", parts);
    model.addAttribute("imageUrl", this.imageUrl);

    return "cycles/show";
  }

}
