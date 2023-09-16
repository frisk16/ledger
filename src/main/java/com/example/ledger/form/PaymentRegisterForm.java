package com.example.ledger.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRegisterForm {
  
  private Integer userId;

  @NotNull(message = "カテゴリーを選択してください")
  private Integer categoryId;

  @NotBlank(message = "支払いタイトルを入力してください")
  @Length(max = 30, message = "30文字以内で設定してください")
  private String name;

  @NotNull(message = "金額を入力してください")
  @Min(value = 1, message = "￥1円以上の金額で設定してください")
  private Integer price;

  @NotBlank(message = "支払い方法を選択してください")
  private String method;

  @NotBlank(message = "日付を選択してください")
  private String date;

}
