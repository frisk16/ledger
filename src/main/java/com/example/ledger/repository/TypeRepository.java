package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Integer> {
  
}
