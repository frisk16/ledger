package com.example.ledger.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordMailForm {
  
  @NotBlank(message = "Eメールアドレスを入力してください")
  private String email;

}
