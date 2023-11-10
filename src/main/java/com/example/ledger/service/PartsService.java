package com.example.ledger.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.example.ledger.entity.Parts;
import com.example.ledger.entity.PartsCategory;
import com.example.ledger.form.PartsRegisterForm;
import com.example.ledger.repository.PartsCategoryRepository;
import com.example.ledger.repository.PartsRepository;

@Service
public class PartsService {

  @Value("${aws.s3.bucket-name}")
  private String bucketName;
  
  private final PartsRepository partsRepository;
  private final PartsCategoryRepository categoryRepository;
  private final AmazonS3 s3Client;

  public PartsService(
    PartsRepository partsRepository,
    PartsCategoryRepository categoryRepository,
    AmazonS3 s3Client
  ) {
    this.partsRepository = partsRepository;
    this.categoryRepository = categoryRepository;
    this.s3Client = s3Client;
  }

  @Transactional
  public void create(PartsRegisterForm registerForm, Integer id) {
    
    MultipartFile imageFile = registerForm.getImage();
    
    String imageName = null;
    if(!imageFile.isEmpty()) {
      imageName = imageFile.getOriginalFilename();
      Path path = Paths.get("src/main/resources/static/img/" + imageName);
      String uploadPath = path.toString();

      // 一旦ローカル上へ画像を保存
      this.copyImageFile(imageFile, path);

      // AWS S3へ画像をアップロード、その後ローカル上の画像を削除
      this.uploadImageFile(imageName, uploadPath);
    }

    PartsCategory category = this.categoryRepository.getReferenceById(id);
    Parts parts = new Parts();

    parts.setPartsCategory(category);
    parts.setName(registerForm.getName());
    parts.setImage(imageName);
    parts.setDescription(registerForm.getDescription());

    this.partsRepository.save(parts);
  }

  // ローカルへ画像を保存
  private void copyImageFile(MultipartFile imageFile, Path path) {
    try {
      Files.copy(imageFile.getInputStream(), path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // AWS S3へ画像をアップロード
  private void uploadImageFile(String imageName, String uploadPath) {
    try {
      File file = new File(uploadPath);
      this.s3Client.putObject(this.bucketName + "/image", imageName, file);
      file.delete();
    } catch (AmazonServiceException e) {
      e.printStackTrace();
    }
  }

}
