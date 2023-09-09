package com.example.ledger.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ledger.entity.User;
import com.example.ledger.entity.VerificationToken;
import com.example.ledger.repository.UserRepository;
import com.example.ledger.repository.VerificationTokenRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;

  public UserDetailsServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
    this.userRepository = userRepository;
    this.verificationTokenRepository = verificationTokenRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    try {
      User user = this.userRepository.findByEmail(email);
      String userRoleName = user.getRole().getName();
      Collection<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(userRoleName));
      
      // トークン初期化
      VerificationToken verificationToken = this.verificationTokenRepository.findByUser(user);
      if(verificationToken != null) {
        this.verificationTokenRepository.deleteById(verificationToken.getId());
      }

      return new UserDetailsImpl(user, authorities);

    } catch (Exception e) {
      throw new UsernameNotFoundException("ユーザーが見つかりませんでした");
    }

  }

}