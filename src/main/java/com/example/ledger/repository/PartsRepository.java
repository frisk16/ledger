package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Parts;

public interface PartsRepository extends JpaRepository<Parts, Integer> {
  
}
