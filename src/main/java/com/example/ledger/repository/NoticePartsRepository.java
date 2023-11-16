package com.example.ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.NoticeParts;
import com.example.ledger.entity.Parts;

public interface NoticePartsRepository extends JpaRepository<NoticeParts, Integer> {
  
  public List<NoticeParts> findByParts(Parts parts);

}
