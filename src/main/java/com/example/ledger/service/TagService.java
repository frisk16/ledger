package com.example.ledger.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Tag;
import com.example.ledger.entity.User;
import com.example.ledger.form.TagRegisterForm;
import com.example.ledger.repository.TagRepository;
import com.example.ledger.repository.UserRepository;

@Service
public class TagService {
  
  private final TagRepository tagRepository;
  private final UserRepository userRepository;

  public TagService(
    TagRepository tagRepository,
    UserRepository userRepository
  ) {
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void create(TagRegisterForm tagRegisterForm) {

    Tag tag = new Tag();
    User user = this.userRepository.getReferenceById(tagRegisterForm.getUserId());
    tag.setUser(user);
    tag.setName(tagRegisterForm.getName());

    this.tagRepository.save(tag);
  }

  @Transactional
  public void update(TagRegisterForm tagRegisterForm, Integer id) {
    User user = this.userRepository.getReferenceById(tagRegisterForm.getUserId());
    Tag tag = this.tagRepository.getReferenceById(id);
    
    tag.setUser(user);
    tag.setName(tagRegisterForm.getName());

    this.tagRepository.save(tag);
  }

  // 既に同名のタグが存在
  public boolean existsName(User user, String name) {
    return this.tagRepository.findByUserAndName(user, name) != null;
  }

}
