package com.example.ledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Parts;
import com.example.ledger.entity.PartsCategory;

public interface PartsRepository extends JpaRepository<Parts, Integer> {
  
  public Page<Parts> findByPartsCategoryOrderByCreatedAtDesc(PartsCategory category, Pageable pageable); 

}
