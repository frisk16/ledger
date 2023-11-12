package com.example.ledger.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

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
  public void create(PartsRegisterForm partsRegisterForm, Integer id) {
    
    MultipartFile imageFile = partsRegisterForm.getImage();
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
    LocalDate exchangedDate = LocalDate.parse(partsRegisterForm.getExchangedDate());
    Parts parts = new Parts();

    parts.setPartsCategory(category);
    parts.setName(partsRegisterForm.getName());
    parts.setImage(imageName);
    parts.setDescription(partsRegisterForm.getDescription());
    parts.setExchangedDate(exchangedDate);

    this.partsRepository.save(parts);
  }

  @Transactional
  public void update(PartsRegisterForm partsEditForm, Integer id) {

    Parts parts = this.partsRepository.getReferenceById(id);
    MultipartFile imageFile = partsEditForm.getImage();
    String imageName = parts.getImage();

    if(!imageFile.isEmpty()) {
      imageName = imageFile.getOriginalFilename();
      Path path = Paths.get("src/main/resources/static/img/" + imageName);
      String uploadPath = path.toString();

      // 一旦ローカル上へコピー
      this.copyImageFile(imageFile, path);
      
      // AWS S3へアップロード
      this.uploadImageFile(imageName, uploadPath);

      // AWS S3上にある古い画像を削除
      if(parts.getImage() != null) {
        this.deleteS3File(parts.getImage());
      }
    }

    LocalDate exchangedDate = LocalDate.parse(partsEditForm.getExchangedDate());
    parts.setName(partsEditForm.getName());
    parts.setImage(imageName);
    parts.setDescription(partsEditForm.getDescription());
    parts.setExchangedDate(exchangedDate);

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

  // AWS S3上の画像を削除
  private void deleteS3File(String imageName) {
    try {
      String imagePath = "image/" + imageName;
      this.s3Client.deleteObject(this.bucketName, imagePath);
    } catch(AmazonServiceException e) {
      e.printStackTrace();
    }
  }

}
