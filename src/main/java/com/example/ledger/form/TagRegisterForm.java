package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagRegisterForm {
  
  private Integer userId;

  @NotBlank(message = "タグ名を入力してください")
  @Length(max = 10, message = "10文字以内で設定してください")
  private String name;

}
