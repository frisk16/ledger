package com.example.ledger.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.Payment;
import com.example.ledger.entity.User;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  
  public Page<Payment> findByUserAndDateBetweenOrderByDateDescCreatedAtDesc(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
  public List<Payment> findByUserAndDateBetweenOrderByDateDescCreatedAtDesc(User user, LocalDate startDate, LocalDate endDate);
  public Page<Payment> findByUserAndCategoryAndDateBetweenOrderByDateDescCreatedAtDesc(User user, Category category, LocalDate startDate, LocalDate endDate, Pageable pageable);
  public List<Payment> findByUserAndCategoryAndDateBetweenOrderByDateDescCreatedAtDesc(User user, Category category, LocalDate startDate, LocalDate endDate);

}
