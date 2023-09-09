package com.example.ledger.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.Payment;
import com.example.ledger.entity.User;
import com.example.ledger.form.PaymentAddForm;
import com.example.ledger.repository.CategoryRepository;
import com.example.ledger.repository.PaymentRepository;
import com.example.ledger.repository.UserRepository;

@Service
public class PaymentService {
  
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final PaymentRepository paymentRepository;

  public PaymentService(
    UserRepository userRepository,
    CategoryRepository categoryRepository,
    PaymentRepository paymentRepository
  ) {
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.paymentRepository = paymentRepository;
  }

  @Transactional
  public void create(PaymentAddForm paymentAddForm) {
    User user = this.userRepository.getReferenceById(paymentAddForm.getUserId());
    Category category = this.categoryRepository.getReferenceById(paymentAddForm.getCategoryId());
    LocalDate date = LocalDate.parse(paymentAddForm.getDate());
    Payment payment = new Payment();

    payment.setUser(user);
    payment.setCategory(category);
    payment.setName(paymentAddForm.getName());
    payment.setPrice(paymentAddForm.getPrice());
    payment.setMethod(paymentAddForm.getMethod());
    payment.setDate(date);
    
    this.paymentRepository.save(payment);
  }

}
