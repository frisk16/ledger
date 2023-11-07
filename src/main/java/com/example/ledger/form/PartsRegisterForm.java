package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartsRegisterForm {
  
  @NotBlank(message = "パーツ名を設定してください")
  @Length(max = 50, message = "50文字以内で設定してください")
  private String name;

  private MultipartFile image;

  @Length(max = 255, message = "255文字以内で設定してください")
  private String description;

}
