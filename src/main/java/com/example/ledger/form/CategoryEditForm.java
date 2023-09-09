package com.example.ledger.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryEditForm {
  
  private Integer id;
  private Integer userId;

  @NotBlank
  private String name;

}
