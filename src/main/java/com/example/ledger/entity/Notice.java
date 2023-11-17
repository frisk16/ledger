package com.example.ledger.entity;

import java.sql.Timestamp;
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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notices")
@Data
public class Notice {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToMany
  @JoinTable(
    name = "notices_parts",
    joinColumns = @JoinColumn(name = "notice_id"),
    inverseJoinColumns = @JoinColumn(name = "parts_id")
  )
  private List<Parts> parts = new ArrayList<>();

  @Column(name = "name")
  private String name;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Timestamp updatedAt;

}