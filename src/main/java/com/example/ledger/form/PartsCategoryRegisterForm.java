package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartsCategoryRegisterForm {
  
  @NotBlank(message = "カテゴリー名を設定してください")
  @Length(max = 20, message = "20文字以内で設定してください")
  private String name;

  private MultipartFile image;

}
