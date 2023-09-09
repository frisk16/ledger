package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  public User findByEmail(String email);

}
