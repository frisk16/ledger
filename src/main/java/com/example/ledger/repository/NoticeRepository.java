package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
  
}
