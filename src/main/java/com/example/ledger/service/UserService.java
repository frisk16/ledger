package com.example.ledger.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Role;
import com.example.ledger.entity.User;
import com.example.ledger.form.ResetPasswordForm;
import com.example.ledger.form.UserRegisterForm;
import com.example.ledger.repository.RoleRepository;
import com.example.ledger.repository.UserRepository;

@Service
public class UserService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
    RoleRepository roleRepository,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder
  ) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User create(UserRegisterForm userRegisterForm) {
    User user = new User();
    Role role = this.roleRepository.findByName("ROLE_GENERAL");
    String password = this.passwordEncording(userRegisterForm.getPassword());

    user.setRole(role);
    user.setName(userRegisterForm.getName());
    user.setEmail(userRegisterForm.getEmail());
    user.setPassword(password);
    user.setAddress(userRegisterForm.getAddress());
    user.setPhoneNumber(userRegisterForm.getPhoneNumber());
    user.setEnabled(false);

    return this.userRepository.save(user);
  }

  @Transactional
  public void editPassword(ResetPasswordForm resetPasswordForm) {
    User user = this.userRepository.getReferenceById(resetPasswordForm.getUserId());
    String password = this.passwordEncording(resetPasswordForm.getPassword());

    user.setPassword(password);

    this.userRepository.save(user);
  }

  // ユーザーの有効化
  @Transactional
  public void enableUser(User user) {
    user.setEnabled(true);
    this.userRepository.save(user);
  }

  // Eメールアドレス登録済み
  public boolean isEmailRegistered(String email) {
    User user = this.userRepository.findByEmail(email);

    return user != null;
  }

  // 確認用パスワード一致
  public boolean isSamePassword(String password, String passwordConfirmation) {
    return password.equals(passwordConfirmation);
  }

  // パスワード暗号化
  private String passwordEncording(String password) {
    return this.passwordEncoder.encode(password);
  }

}
