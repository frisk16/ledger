package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordForm {
  
  private String token;
  
  private Integer userId;

  @NotBlank
  @Length(min = 8)
  private String password;

  @NotBlank
  private String passwordConfirmation;

}
