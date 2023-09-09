package com.example.ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.User;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
  
  public List<Category> findByUser(User user);
  public Category findByUserAndName(User user, String name);

}
