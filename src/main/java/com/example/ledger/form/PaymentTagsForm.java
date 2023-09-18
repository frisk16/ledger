package com.example.ledger.form;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PaymentTagsForm {
  
  @NotEmpty
  private List<Integer> tagIds = new ArrayList<>();

}
