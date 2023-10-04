package com.example.ledger.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.User;
import com.example.ledger.form.UserEditForm;
import com.example.ledger.form.UserEditPasswordForm;
import com.example.ledger.repository.UserRepository;

@Service
public class GeneralUserService {
  
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public GeneralUserService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void update(UserEditForm userEditForm, Integer userId) {

    User user = this.userRepository.getReferenceById(userId);

    user.setName(userEditForm.getName());
    user.setEmail(userEditForm.getEmail());
    user.setAddress(userEditForm.getAddress());
    user.setPhoneNumber(userEditForm.getPhoneNumber());

    this.userRepository.save(user);
  }

  @Transactional
  public void updatePassword(UserEditPasswordForm userEditPasswordForm, Integer userId) {
    User user = this.userRepository.getReferenceById(userId);

    user.setPassword(this.passwordEncoding(userEditPasswordForm.getNewPassword()));

    this.userRepository.save(user);
  }

  // 既に登録済みのユーザー名が存在
  public boolean existsName(String currentName, String newName) {
    User user = this.userRepository.findByName(newName);
    return !currentName.equals(newName) && user != null;
  }

  // 既に登録済みのEメールアドレスが存在
  public boolean existsEmail(String currentEmail, String newEmail) {
    User user = this.userRepository.findByEmail(newEmail);
    return !currentEmail.equals(newEmail) && user != null;
  }

  // 現在のパスワードを検証
  public boolean verifyPassword(String currentPassword, Integer userId) {
    User user = this.userRepository.getReferenceById(userId);
    String encryptPassword = this.passwordEncoding(currentPassword);
    
    return user.getPassword() == encryptPassword;
  }

  // 確認用パスワードと等しい
  public boolean samePasswordConfirmation(String newPassword, String passwordConfirmation) {
    return newPassword == passwordConfirmation;
  }

  // パスワード暗号化
  public String passwordEncoding(String password) {
    return this.passwordEncoder.encode(password);
  }

}
