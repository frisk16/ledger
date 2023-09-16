package com.example.ledger.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.Category;
import com.example.ledger.entity.Payment;
import com.example.ledger.entity.User;
import com.example.ledger.form.PaymentRegisterForm;
import com.example.ledger.repository.CategoryRepository;
import com.example.ledger.repository.PaymentRepository;
import com.example.ledger.security.UserDetailsImpl;
import com.example.ledger.service.PaymentService;

@Controller
@RequestMapping("/payments")
public class PaymentController {

  private final CategoryRepository categoryRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentService paymentService;

  public PaymentController(
    CategoryRepository categoryRepository,
    PaymentRepository paymentRepository,
    PaymentService paymentService
  ) {
    this.categoryRepository = categoryRepository;
    this.paymentRepository = paymentRepository;
    this.paymentService = paymentService;
  }
  
  @GetMapping
  public String index(
    @RequestParam(name = "year", required = false) Integer year,
    @RequestParam(name = "month", required = false) Integer month,
    @RequestParam(name = "categoryId", required = false) Integer categoryId,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Category> categories = this.categoryRepository.findByUser(user);
    LocalDate currentDate = LocalDate.now();
    if(year == null) {
      year = currentDate.getYear();
    }
    if(month == null || month < 1 || month > 12) {
      month = currentDate.getMonthValue();
    }
    
    String insertYear = year.toString();
    String insertMonth;
    if(month < 10) {
      insertMonth = "0" + month;
    } else {
      insertMonth = month.toString();
    }
    LocalDate startDate = LocalDate.parse(insertYear + "-" + insertMonth + "-01");
    LocalDate endDate = startDate.plusMonths(1).minusDays(1);
    
    Page<Payment> payments;
    if(categoryId != null) {
      Category category = this.categoryRepository.getReferenceById(categoryId);
      payments = this.paymentRepository.findByUserAndCategoryAndDateBetweenOrderByDateDescCreatedAtDesc(user, category, startDate, endDate, pageable);
    } else {
      payments = this.paymentRepository.findByUserAndDateBetweenOrderByDateDescCreatedAtDesc(user, startDate, endDate, pageable);
    }

    model.addAttribute("categories", categories);
    model.addAttribute("payments", payments);
    model.addAttribute("methodIcons", this.methodIcons());
    model.addAttribute("year", year);
    model.addAttribute("month", month);
    model.addAttribute("categoryId", categoryId);
    
    return "payments/index";
  }

  @GetMapping("/add")
  public String add(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Category> categories = this.categoryRepository.findByUser(user);
    PaymentRegisterForm paymentRegisterForm = new PaymentRegisterForm(user.getId(), null, null, null, null, null);

    model.addAttribute("paymentRegisterForm", paymentRegisterForm);
    model.addAttribute("categories", categories);
    model.addAttribute("methods", this.allMethods());

    return "payments/add";
  }

  @PostMapping("/create")
  public String create(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @ModelAttribute @Validated PaymentRegisterForm paymentRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    List<Category> categories = this.categoryRepository.findByUser(user);

    if(bindingResult.hasErrors()) {
      model.addAttribute("categories", categories);
      model.addAttribute("methods", this.allMethods());
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      return "payments/add";
    }

    this.paymentService.create(paymentRegisterForm);
    redirectAttributes.addFlashAttribute("successMsg", "支払いデータを追加しました");    

    return "redirect:/payments";
  }

  @GetMapping("/{id}/edit")
  public String edit(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {
    User user = userDetailsImpl.getUser();
    Payment payment = this.paymentRepository.getReferenceById(id);
    if(payment.getUser().getId() != user.getId()) {
      return "redirect:/payments";
    }

    List<Category> categories = this.categoryRepository.findByUser(user);
    PaymentRegisterForm paymentRegisterForm = new PaymentRegisterForm(
      user.getId(),
      payment.getCategory().getId(),
      payment.getName(),
      payment.getPrice(),
      payment.getMethod(),
      payment.getDate().toString() 
    );
    model.addAttribute("payment", payment);
    model.addAttribute("paymentRegisterForm", paymentRegisterForm);
    model.addAttribute("categories", categories);
    model.addAttribute("methods", this.allMethods());

    return "payments/edit";
  }

  @PostMapping("/{id}/update")
  public String update(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @ModelAttribute @Validated PaymentRegisterForm paymentRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {
    if(bindingResult.hasErrors()) {
      User user = userDetailsImpl.getUser();
      List<Category> categories = this.categoryRepository.findByUser(user);

      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      model.addAttribute("categories", categories);
      model.addAttribute("methods", this.allMethods());

      return "payments/edit";
    }

    this.paymentService.update(paymentRegisterForm, id);
    redirectAttributes.addFlashAttribute("successMsg", "支払いデータを更新しました");

    return "redirect:/payments";
  }

  @PostMapping("/{id}/delete")
  public String delete(
    @PathVariable(name = "id") Integer id,
    RedirectAttributes redirectAttributes
  ) {
    this.paymentRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("successMsg", "支払いデータを削除しました");

    return "redirect:/payments";
  }

  // 支払い方法のリスト
  private String[] allMethods() {
    String[] methods = {
      "現金",
      "クレジット",
      "電子マネー",
      "銀行振込",
      "ＱＲコード",
    };

    return methods;
  }

  // 支払い方法のアイコン
  private HashMap<String, String> methodIcons() {
    HashMap<String, String> methodIcons = new HashMap<>();

    methodIcons.put("現金", "fa-3x fa-solid fa-coins");
    methodIcons.put("クレジット", "fa-3x fa-solid fa-credit-card");
    methodIcons.put("電子マネー", "fa-3x fa-solid fa-mobile-screen-button");
    methodIcons.put("銀行振込", "fa-3x fa-solid fa-building-columns");
    methodIcons.put("ＱＲコード", "fa-3x fa-solid fa-barcode");

    return methodIcons;
  }

}
