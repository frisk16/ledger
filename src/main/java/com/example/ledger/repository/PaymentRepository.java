package com.example.ledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Payment;
import com.example.ledger.entity.User;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  
  public Page<Payment> findByUser(User user, Pageable pageable);

}
