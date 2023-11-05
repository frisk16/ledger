package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.PartsCategory;

public interface PartsCategoryRepository extends JpaRepository<PartsCategory, Integer> {
  
  public PartsCategory findByName(String name);

}
