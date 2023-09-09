package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.User;
import com.example.ledger.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
  
  public VerificationToken findByToken(String token);
  public VerificationToken findByUser(User user);

}
