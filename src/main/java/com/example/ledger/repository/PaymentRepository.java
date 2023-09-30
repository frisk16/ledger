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
  public Page<Payment> findByUserAndDateBetweenOrderByDateAscCreatedAtAsc(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
  public Page<Payment> findByUserAndCategoryAndDateBetweenOrderByDateDescCreatedAtDesc(User user, Category category, LocalDate startDate, LocalDate endDate, Pageable pageable);
  public Page<Payment> findByUserAndCategoryAndDateBetweenOrderByDateAscCreatedAtAsc(User user, Category category, LocalDate startDate, LocalDate endDate, Pageable pageable);
  public Page<Payment> findByUserAndDateBetweenAndNameLikeOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate, String name, Pageable pageable);
  public Page<Payment> findByUserAndDateBetweenAndNameLikeOrderByDateAsc(User user, LocalDate startDate, LocalDate endDate, String name, Pageable pageable);

  public List<Payment> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
  public List<Payment> findByUserAndCategoryAndDateBetween(User user, Category category, LocalDate startDate, LocalDate endDate);


}
