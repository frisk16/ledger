package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Integer> {
  
}
