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
@Table(name = "parts")
@Data
public class Parts {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "parts_category_id")
  private PartsCategory partsCategory;

  @ManyToMany
  @JoinTable(
    name = "notices_parts",
    joinColumns = @JoinColumn(name = "parts_id"),
    inverseJoinColumns = @JoinColumn(name = "notice_id")
  )
  private List<Notice> notices = new ArrayList<>();

  @Column(name = "name")
  private String name;

  @Column(name = "image")
  private String image;

  @Column(name = "description")
  private String description;

  @Column(name = "exchanged_date")
  private LocalDate exchangedDate;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Timestamp updatedAt;

}
