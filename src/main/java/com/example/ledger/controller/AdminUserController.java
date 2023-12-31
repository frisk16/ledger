package com.example.ledger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ledger.entity.Notice;
import com.example.ledger.entity.Parts;
import com.example.ledger.entity.PartsCategory;
import com.example.ledger.entity.User;
import com.example.ledger.form.AddNoticeForm;
import com.example.ledger.form.AdminMemberEditForm;
import com.example.ledger.form.PartsCategoryRegisterForm;
import com.example.ledger.form.PartsRegisterForm;
import com.example.ledger.repository.NoticeRepository;
import com.example.ledger.repository.PartsCategoryRepository;
import com.example.ledger.repository.PartsRepository;
import com.example.ledger.repository.UserRepository;
import com.example.ledger.service.NoticeService;
import com.example.ledger.service.PartsCategoryService;
import com.example.ledger.service.PartsService;
import com.example.ledger.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminUserController {
  
  @Value("${aws.s3.image-folder-url}")
  private String imageUrl;

  private final UserRepository userRepository;
  private final UserService userService;
  private final PartsCategoryRepository partsCategoryRepository;
  private final PartsCategoryService partsCategoryService;
  private final PartsRepository partsRepository;
  private final PartsService partsService;
  private final NoticeService noticeService;
  private final NoticeRepository noticeRepository;
  
  public AdminUserController(
    UserRepository userRepository,
    UserService userService,
    PartsCategoryRepository partsCategoryRepository,
    PartsCategoryService partsCategoryService,
    PartsRepository partsRepository,
    PartsService partsService,
    NoticeService noticeService,
    NoticeRepository noticeRepository
    ) {
      this.userRepository = userRepository;
      this.userService = userService;
      this.partsCategoryRepository = partsCategoryRepository;
      this.partsCategoryService = partsCategoryService;
      this.partsRepository = partsRepository;
      this.partsService = partsService;
      this.noticeService = noticeService;
      this.noticeRepository = noticeRepository;
    }

    @GetMapping
    public String index() {
      
    return "users/admin/index";
  }


  // ----------------------------
  // ------ Members -------------
  // ----------------------------
  @GetMapping("/members")
  public String membersIndex(
    @RequestParam(name = "sort", required = false) String sort,
    @RequestParam(name = "keyword", required = false) String keyword,
    @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
    Model model
  ) {

    Page<User> users;
    if(keyword != null && sort != null) {
      if(sort.equals("ASC")) {
        users = this.userRepository.findByNameLikeOrEmailLikeOrderByCreatedAtAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
      } else {
        users = this.userRepository.findByNameLikeOrEmailLike("%" + keyword + "%", "%" + keyword + "%", pageable);
      }
    } else if(sort != null) {
      if(sort.equals("ASC")) {
        users = this.userRepository.findAllByOrderByCreatedAtAsc(pageable);
      } else {
        users = this.userRepository.findAll(pageable);
      }
    } else if(keyword != null) {
      users = this.userRepository.findByNameLikeOrEmailLike("%" + keyword + "%", "%" + keyword + "%", pageable);
    } else {
      users = this.userRepository.findAll(pageable);
    }

    model.addAttribute("keyword", keyword);
    model.addAttribute("sort", sort);
    model.addAttribute("users", users);

    return "users/admin/members/index";
  }


  @GetMapping("/members/{id}")
  public String membersShow(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);

    model.addAttribute("user", user);

    return "users/admin/members/show";
  }


  @GetMapping("/members/{id}/edit")
  public String membersEdit(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);

    AdminMemberEditForm adminMemberEditForm = new AdminMemberEditForm(
      user.getName(),
      user.getEmail(),
      user.getAddress(),
      user.getPhoneNumber(),
      user.getPassword(),
      user.getEnabled()
    );

    model.addAttribute("user", user);
    model.addAttribute("adminMemberEditForm", adminMemberEditForm);

    return "users/admin/members/edit";
  }


  @PostMapping("/members/{id}/update")
  public String membersUpdate(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated AdminMemberEditForm adminMemberEditForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {
    User user = this.userRepository.getReferenceById(id);
    String newName = adminMemberEditForm.getName();
    String newEmail = adminMemberEditForm.getEmail();

    if(this.userService.existsName(user.getName(), newName)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "そのユーザー名は既に使用されています");
      bindingResult.addError(fieldError);
    }
    if(this.userService.existsEmail(user.getEmail(), newEmail)) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に使用されています");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      model.addAttribute("user", user);

      return "users/admin/members/edit";
    }

    this.userService.membersUpdate(adminMemberEditForm, user);
    redirectAttributes.addFlashAttribute("successMsg", "会員情報を更新しました");

    return "redirect:/admin/members/{id}";
  }


  // ----------------------------
  // ------ Parts ---------------
  // ----------------------------
  @GetMapping("/parts")
  public String partsIndex(Model model) {

    List<PartsCategory> partsCategories = this.partsCategoryRepository.findAll();
    PartsCategoryRegisterForm registerForm = new PartsCategoryRegisterForm(null, null);

    model.addAttribute("partsCategories", partsCategories);
    model.addAttribute("registerForm", registerForm);
    model.addAttribute("imageUrl", this.imageUrl);

    return "users/admin/parts/index";
  }


  @PostMapping("/parts/categoryCreate")
  public String partsCategoryCreate(
    @ModelAttribute PartsCategoryRegisterForm registerForm,
    RedirectAttributes redirectAttributes
  ) {
    if(this.partsCategoryService.isOrverLength(registerForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "20文字以内で設定してください");
      return "redirect:/admin/parts";
    }

    this.partsCategoryService.create(registerForm);
    redirectAttributes.addFlashAttribute("successMsg", "カテゴリーを追加しました");

    return "redirect:/admin/parts";
  }


  @GetMapping("/parts/{id}/showCategory")
  public String showCategory(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    PartsCategory category = this.partsCategoryRepository.getReferenceById(id);
    List<Parts> parts = category.getParts();
    PartsCategoryRegisterForm editForm = new PartsCategoryRegisterForm(category.getName(), null);

    model.addAttribute("category", category);
    model.addAttribute("parts", parts);
    model.addAttribute("editForm", editForm);
    model.addAttribute("imageUrl", this.imageUrl);

    return "users/admin/parts/showCategory";
  }


  @PostMapping("/parts/{id}/updateCategory")
  public String updateCategory(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute PartsCategoryRegisterForm editForm,
    RedirectAttributes redirectAttributes
  ) {
    
    if(this.partsCategoryService.isOrverLength(editForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "20文字以内で設定してください");
      return "redirect:/admin/parts/{id}/showCategory";
    }
    if(this.partsCategoryService.existsName(id, editForm.getName())) {
      redirectAttributes.addFlashAttribute("errorMsg", "そのカテゴリー名は既に存在します");
      return "redirect:/admin/parts/{id}/showCategory";
    }

    this.partsCategoryService.update(editForm, id);
    redirectAttributes.addFlashAttribute("successMsg", "カテゴリー情報を更新しました");

    return "redirect:/admin/parts/{id}/showCategory";

  }


  @GetMapping("/parts/{id}/registerParts")
  public String registerParts(
    @PathVariable(name = "id") Integer id,
    Model model
  ) {
    PartsCategory partsCategory = this.partsCategoryRepository.getReferenceById(id);
    PartsRegisterForm partsRegisterForm = new PartsRegisterForm(null, null, null, null);

    model.addAttribute("partsCategory", partsCategory);
    model.addAttribute("partsRegisterForm", partsRegisterForm);

    return "users/admin/parts/registerParts";
  }


  @PostMapping("/parts/{id}/createParts")
  public String createParts(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated PartsRegisterForm partsRegisterForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {
    
    if(bindingResult.hasErrors()) {
      PartsCategory partsCategory = this.partsCategoryRepository.getReferenceById(id);
      model.addAttribute("partsCategory", partsCategory);
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      return "users/admin/parts/registerParts";
    }

    this.partsService.create(partsRegisterForm, id);
    redirectAttributes.addFlashAttribute("successMsg", "パーツを追加しました");

    return "redirect:/admin/parts/{id}/showCategory";
  }


  @GetMapping("/parts/{categoryId}/{partsId}/edit")
  public String editParts(
    @PathVariable(name = "partsId") Integer partsId,
    Model model
  ) {
    
    Parts parts = this.partsRepository.getReferenceById(partsId);
    String exchangedDate = parts.getExchangedDate().toString();
    PartsRegisterForm partsEditForm = new PartsRegisterForm(parts.getName(), null, parts.getDescription(), exchangedDate);
    AddNoticeForm addNoticeForm = new AddNoticeForm();
    List<Notice> notices = this.noticeRepository.findAll();

    model.addAttribute("parts", parts);
    model.addAttribute("partsEditForm", partsEditForm);
    model.addAttribute("addNoticeForm", addNoticeForm);
    model.addAttribute("notices", notices);

    return "users/admin/parts/editParts";
  }


  @PostMapping("/parts/{categoryId}/{partsId}/update")
  public String updateParts(
    @PathVariable(name = "partsId") Integer partsId,
    @ModelAttribute @Validated PartsRegisterForm partsEditForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    if(bindingResult.hasErrors()) {
      Parts parts = this.partsRepository.getReferenceById(partsId);
      model.addAttribute("parts", parts);
      model.addAttribute("errorMsg", "入力内容に誤りがあります");
      return "users/admin/parts/editParts";
    }

    this.partsService.update(partsEditForm, partsId);
    redirectAttributes.addFlashAttribute("successMsg", "パーツ情報を更新しました");
    
    return "redirect:/admin/parts/{categoryId}/showCategory";
  }

  
  @PostMapping("/parts/{categoryId}/{partsId}/delete")
  public String deleteParts(
    @PathVariable(name = "partsId") Integer partsId,
    RedirectAttributes redirectAttributes
  ) {
    
    this.partsRepository.deleteById(partsId);
    redirectAttributes.addFlashAttribute("successMsg", "パーツを削除しました");

    return "redirect:/admin/parts/{categoryId}/showCategory";
  }

  @PostMapping("/parts/{categoryId}/{partsId}/addNotice")
  public String addNotice(
    @PathVariable(name = "partsId") Integer partsId,
    @ModelAttribute @Validated AddNoticeForm addNoticeForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    if(bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("errorMsg", "通知を選択してください");
      return "redirect:/admin/parts/{categoryId}/{partsId}/edit";
    }

    this.noticeService.add(addNoticeForm, partsId);
    redirectAttributes.addFlashAttribute("successMsg", "通知を追加しました");

    return "redirect:/admin/parts/{categoryId}/{partsId}/edit";
  }


  @PostMapping("/parts/{categoryId}/{partsId}/deleteNotice")
  public String deleteNotice(
    @PathVariable(name = "partsId") Integer partsId,
    RedirectAttributes redirectAttributes
  ) {

    this.noticeService.delete(partsId);
    redirectAttributes.addFlashAttribute("successMsg", "通知を削除しました");

    return "redirect:/admin/parts/{categoryId}/{partsId}/edit";
  }


}
