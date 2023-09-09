package com.example.ledger.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.User;
import com.example.ledger.form.CategoryAddForm;
import com.example.ledger.form.CategoryEditForm;
import com.example.ledger.repository.CategoryRepository;
import com.example.ledger.repository.UserRepository;

@Service
public class CategoryService {
  
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public CategoryService(
    CategoryRepository categoryRepository,
    UserRepository userRepository
    ) {
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void create(CategoryAddForm categoryAddForm) {
    User user = this.userRepository.getReferenceById(categoryAddForm.getUserId());
    Category category = new Category();

    category.setUser(user);
    category.setName(categoryAddForm.getName());

    this.categoryRepository.save(category);
  }

  @Transactional
  public void update(CategoryEditForm categoryEditForm) {
    Category category = this.categoryRepository.getReferenceById(categoryEditForm.getId());

    category.setName(categoryEditForm.getName());

    this.categoryRepository.save(category);
  }

  // カテゴリー名が空
  public boolean isNameEmpty(String name) {
    return name.isEmpty();
  }

  // カテゴリー名が20文字超え
  public boolean isNameOfOverLength(String name) {
    return name.length() > 20;
  }

  // 同じカテゴリーが既に存在する
  public boolean existsName(Integer userId, String name) {
    User user = this.userRepository.getReferenceById(userId);
    Category category = this.categoryRepository.findByUserAndName(user, name);

    return category != null;
  }
}
