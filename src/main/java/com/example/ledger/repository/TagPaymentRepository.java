package com.example.ledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Payment;
import com.example.ledger.entity.Tag;
import com.example.ledger.entity.TagPayment;

public interface TagPaymentRepository extends JpaRepository<TagPayment, Integer> {
  
  public TagPayment findByTagAndPayment(Tag tag, Payment payment);
  public Page<TagPayment> findByTag(Tag tag, Pageable pageable);
  
}
