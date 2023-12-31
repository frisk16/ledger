package com.example.ledger.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToMany
  @JoinTable(
    name = "tag_payment",
    joinColumns = @JoinColumn(name = "payment_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private List<Tag> tags = new ArrayList<>();

  @Column(name = "name")
  private String name;

  @Column(name = "price")
  private Integer price;

  @Column(name = "method")
  private String method;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Timestamp updatedAt;

}
