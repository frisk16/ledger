package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminMemberEditForm {

  @NotBlank(message = "ユーザー名を入力してください")
  @Length(min = 2, message = "最低2文字以上で設定してください")
  @Length(max = 10, message = "10文字以内で設定してください")
  private String name;

  @NotBlank(message = "Eメールアドレスを入力してください")
  @Email(message = "正しい形式で入力してください")
  private String email;

  @NotBlank(message = "住所を入力してください")
  private String address;

  @NotBlank(message = "電話番号を入力してください")
  private String phoneNumber;

  @NotBlank(message = "パスワードを設定してください")
  @Length(min = 8, message = "最低8文字以上で設定してください")
  private String password;

  @NotNull
  private Boolean enabled;

}
