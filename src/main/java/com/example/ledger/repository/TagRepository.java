package com.example.ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Tag;
import com.example.ledger.entity.User;

public interface TagRepository extends JpaRepository<Tag, Integer> {
  
  public List<Tag> findByUserOrderByCreatedAtDesc(User user);
  public Tag findByUserAndName(User user, String name);

}
