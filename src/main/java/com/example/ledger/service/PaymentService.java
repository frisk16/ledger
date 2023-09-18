package com.example.ledger.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.Payment;
import com.example.ledger.entity.Tag;
import com.example.ledger.entity.TagPayment;
import com.example.ledger.entity.User;
import com.example.ledger.form.PaymentTagsForm;
import com.example.ledger.form.PaymentRegisterForm;
import com.example.ledger.repository.CategoryRepository;
import com.example.ledger.repository.PaymentRepository;
import com.example.ledger.repository.TagPaymentRepository;
import com.example.ledger.repository.TagRepository;
import com.example.ledger.repository.UserRepository;

@Service
public class PaymentService {
  
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final PaymentRepository paymentRepository;
  private final TagRepository tagRepository;
  private final TagPaymentRepository tagPaymentRepository;

  public PaymentService(
    UserRepository userRepository,
    CategoryRepository categoryRepository,
    PaymentRepository paymentRepository,
    TagRepository tagRepository,
    TagPaymentRepository tagPaymentRepository
  ) {
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.paymentRepository = paymentRepository;
    this.tagRepository = tagRepository;
    this.tagPaymentRepository = tagPaymentRepository;
  }

  @Transactional
  public void create(PaymentRegisterForm paymentAddForm) {
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

  @Transactional
  public void update(PaymentRegisterForm paymentRegisterForm, Integer id) {
    Payment payment = this.paymentRepository.getReferenceById(id);
    User user = this.userRepository.getReferenceById(paymentRegisterForm.getUserId());
    Category category = this.categoryRepository.getReferenceById(paymentRegisterForm.getCategoryId());
    LocalDate date = LocalDate.parse(paymentRegisterForm.getDate());

    payment.setUser(user);
    payment.setCategory(category);
    payment.setName(paymentRegisterForm.getName());
    payment.setPrice(paymentRegisterForm.getPrice());
    payment.setMethod(paymentRegisterForm.getMethod());
    payment.setDate(date);

    this.paymentRepository.save(payment);
  }

  @Transactional
  public void addTags(PaymentTagsForm paymentTagsForm, Integer id) {
    Payment payment = this.paymentRepository.getReferenceById(id);
    List<Tag> tags = new ArrayList<>();

    for(Integer tagId : paymentTagsForm.getTagIds()) {
      Tag tag = this.tagRepository.getReferenceById(tagId);
      tags.add(tag);
    }
    payment.setTags(tags);
    
    this.paymentRepository.save(payment);
  }

  public void deleteTags(PaymentTagsForm paymentTagsForm, Integer id) {
    Payment payment = this.paymentRepository.getReferenceById(id);
    Tag tag;
    TagPayment tagPayment;
    for(Integer tagId : paymentTagsForm.getTagIds()) {
      tag = this.tagRepository.getReferenceById(tagId);
      tagPayment = this.tagPaymentRepository.findByTagAndPayment(tag, payment);
      this.tagPaymentRepository.deleteById(tagPayment.getId());
    }
  }

}
