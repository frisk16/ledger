package com.example.ledger.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.User;
import com.example.ledger.entity.VerificationToken;
import com.example.ledger.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
  
  private final VerificationTokenRepository verificationTokenRepository;

  public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
    this.verificationTokenRepository = verificationTokenRepository;
  }

  @Transactional
  public void create(User user, String token) {
    VerificationToken verificationToken = new VerificationToken();

    verificationToken.setUser(user);
    verificationToken.setToken(token);

    this.verificationTokenRepository.save(verificationToken);
  }

  // トークンが一致するかどうか
  public VerificationToken getVerificationToken(String token) {
    return this.verificationTokenRepository.findByToken(token);
  }

}
