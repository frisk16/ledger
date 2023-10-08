package com.example.ledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  public Page<User> findAll(Pageable pageable);
  public User findByEmail(String email);
  public User findByName(String name);

}
