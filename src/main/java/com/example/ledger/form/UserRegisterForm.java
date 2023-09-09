package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterForm {

  @NotBlank(message = "ユーザー名を入力してください")
  @Length(min = 2, message = "最低2文字以上で設定してください")
  @Length(max = 10, message = "10文字以内で設定してください")
  private String name;

  @NotBlank(message = "Eメールアドレスを入力してください")
  @Email(message = "正しい形式で入力してください")
  private String email;

  @NotBlank(message = "パスワードを入力してください")
  @Length(min = 8, message = "最低8文字以上で設定してください")
  private String password;

  @NotBlank(message = "確認用パスワードを入力してください")
  private String passwordConfirmation;

  @NotBlank(message = "住所を入力してください")
  private String address;

  @NotBlank(message = "電話番号を入力してください")
  private String phoneNumber;

}
