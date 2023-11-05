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
import com.example.ledger.entity.PartsCategory;
import com.example.ledger.form.PartsCategoryRegisterForm;
import com.example.ledger.repository.PartsCategoryRepository;

@Service
public class PartsCategoryService {

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  private final PartsCategoryRepository partsCategoryRepository;
  private final AmazonS3 s3Client;

  public PartsCategoryService(
    PartsCategoryRepository partsCategoryRepository,
    AmazonS3 s3Client
    ) {
    this.partsCategoryRepository = partsCategoryRepository;
    this.s3Client = s3Client;
  }

  @Transactional
  public void create(PartsCategoryRegisterForm registerForm) {
    MultipartFile imageFile = registerForm.getImage();
    String imageName = null;

    if(!imageFile.isEmpty()) {
      imageName = imageFile.getOriginalFilename();
      Path filePath = Paths.get("classes/static/storage/" + imageName);
      String uploadFilePath = filePath.toString();
      
      // 一旦ローカルストレージへ画像を保存
      this.copyImageFile(imageFile, filePath);

      // ローカルに保存した画像をS3へアップロード
      this.imageUpload(imageName, uploadFilePath);
    }
    PartsCategory partsCategory = new PartsCategory();

    partsCategory.setName(registerForm.getName());
    partsCategory.setImage(imageName);

    this.partsCategoryRepository.save(partsCategory);
  }

  @Transactional
  public void update(PartsCategoryRegisterForm editForm, Integer id) {
    PartsCategory category = this.partsCategoryRepository.getReferenceById(id);
    String imageName = category.getImage();
    MultipartFile imageFile = editForm.getImage();

    if(!imageFile.isEmpty()) {
      imageName = imageFile.getOriginalFilename();
      
      Path filePath = Paths.get("classes/static/storage/" + imageName);
      String uploadFilePath = filePath.toString();

      // 一旦ローカルストレージへ画像を保存
      this.copyImageFile(imageFile, filePath);

      // S3上にある古い画像を削除
      if(category.getImage() != null) {
        this.deleteS3File(category.getImage());
      }

      // ローカルに保存した画像をS3へアップロード
      this.imageUpload(imageName, uploadFilePath);
    }

    category.setName(editForm.getName());
    category.setImage(imageName);

    this.partsCategoryRepository.save(category);
  }

  // 20文字超え
  public boolean isOrverLength(String name) {
    return name.length() > 20;
  }

  // 同名のカテゴリーが既に存在
  public boolean existsName(Integer id, String name) {
    String currentName = this.partsCategoryRepository.getReferenceById(id).getName();
    return !name.equals(currentName) && this.partsCategoryRepository.findByName(name) != null;
  }

  // ローカル上の指定フォルダへ画像保存
  private void copyImageFile(MultipartFile imageFile, Path filePath) {
    try {
      Files.copy(imageFile.getInputStream(), filePath);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  // AWS S3へ画像をアップロード
  private void imageUpload(String imageName, String uploadFilePath) {
    try {
      File file = new File(uploadFilePath);
      this.s3Client.putObject(this.bucketName + "/image", imageName, file);
      file.delete();
    } catch (AmazonServiceException e) {
      e.printStackTrace();
    }
  }

  // AWS S3上にある指定ファイルを削除
  private void deleteS3File(String imageName) {
    try {
      String imagePath = "image/" + imageName;
      this.s3Client.deleteObject(this.bucketName, imagePath);
    } catch (AmazonServiceException e) {
      e.printStackTrace();
    }
  }

}
