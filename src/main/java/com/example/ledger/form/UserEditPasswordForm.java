package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserEditPasswordForm {
  
  @NotBlank(message = "現在のパスワードを入力してください")
  private String currentPassword;

  @NotBlank(message = "新しいパスワードを入力してください")
  @Length(min = 8, message = "最低8文字以上で設定してください")
  private String newPassword;

  @NotBlank(message = "確認用パスワードを入力してください")
  private String passwordConfirmation;

}
