package com.example.ledger.form;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddNoticeForm {
  
  @NotEmpty
  private List<Integer> noticeIds = new ArrayList<>();

}
