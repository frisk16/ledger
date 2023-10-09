package com.example.ledger.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Role;
import com.example.ledger.entity.User;
import com.example.ledger.form.AdminMemberEditForm;
import com.example.ledger.form.ResetPasswordForm;
import com.example.ledger.form.UserEditForm;
import com.example.ledger.form.UserEditPasswordForm;
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

  // 会員登録
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

  // マイページ｜登録情報変更
  @Transactional
  public void update(UserEditForm userEditForm, User user) {

    user.setName(userEditForm.getName());
    user.setEmail(userEditForm.getEmail());
    user.setAddress(userEditForm.getAddress());
    user.setPhoneNumber(userEditForm.getPhoneNumber());

    this.userRepository.save(user);
  }

  // 管理ページ｜会員情報変更
  @Transactional
  public void membersUpdate(AdminMemberEditForm adminMemberEditForm, User user) {

    String password;
    if(this.isPasswordNonChanged(adminMemberEditForm.getPassword(), user)) {
      password = user.getPassword();
    } else {
      password = this.passwordEncording(adminMemberEditForm.getPassword());
    }

    user.setName(adminMemberEditForm.getName());
    user.setEmail(adminMemberEditForm.getEmail());
    user.setAddress(adminMemberEditForm.getAddress());
    user.setPhoneNumber(adminMemberEditForm.getPhoneNumber());
    user.setPassword(password);
    user.setEnabled(adminMemberEditForm.getEnabled());

    this.userRepository.save(user);
  }

  // マイページ｜パスワード変更
  @Transactional
  public void updatePassword(UserEditPasswordForm userEditPasswordForm, Integer userId) {
    User user = this.userRepository.getReferenceById(userId);

    user.setPassword(this.passwordEncording(userEditPasswordForm.getNewPassword()));

    this.userRepository.save(user);
  }

  // ログイン｜パスワード再設定
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

  // ------------------
  // --  Validation  --
  // ------------------

  // 会員登録｜Eメールアドレス登録済み
  public boolean isEmailRegistered(String email) {
    User user = this.userRepository.findByEmail(email);

    return user != null;
  }
  
  // マイページ登録情報編集｜Eメールアドレス登録済み（自身を除く）
  public boolean existsEmail(String currentEmail, String newEmail) {
    User user = this.userRepository.findByEmail(newEmail);
    return !currentEmail.equals(newEmail) && user != null;
  }

  // ユーザー名登録済み（自身を除く）
  public boolean existsName(String currentName, String newName) {
    User user = this.userRepository.findByName(newName);
    return !currentName.equals(newName) && user != null;
  }
  
  // 現在のパスワードと一致
  public boolean verifyPassword(String currentPassword, User user) {
    return this.passwordEncoder.matches(currentPassword, user.getPassword());
  }

  // 新パスワードと確認用パスワードが一致
  public boolean samePasswordConfirmation(String newPassword, String passwordConfirmation) {
    return newPassword.equals(passwordConfirmation);
  }

  // パスワード未変更
  public boolean isPasswordNonChanged(String password, User user) {
    return password.equals(user.getPassword());
  }

  // パスワード暗号化
  private String passwordEncording(String password) {
    return this.passwordEncoder.encode(password);
  }

}
