package com.example.ledger.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "parts")
@Data
public class Parts {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private Type type;

  @Column(name = "name")
  private String name;

  @Column(name = "image_name")
  private String imageName;

  @Column(name = "price")
  private Integer price;

  @Column(name = "exchange_date")
  private LocalDate exchangeDate;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Timestamp updatedAt;

}
