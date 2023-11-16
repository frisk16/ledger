package com.example.ledger.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ledger.entity.Notice;
import com.example.ledger.entity.NoticeParts;
import com.example.ledger.entity.Parts;
import com.example.ledger.form.AddNoticeForm;
import com.example.ledger.repository.NoticePartsRepository;
import com.example.ledger.repository.NoticeRepository;
import com.example.ledger.repository.PartsRepository;

@Service
public class NoticeService {
  
  private final NoticeRepository noticeRepository;
  private final PartsRepository partsRepository;
  private final NoticePartsRepository noticePartsRepository;

  public NoticeService(
    NoticeRepository noticeRepository,
    PartsRepository partsRepository,
    NoticePartsRepository noticePartsRepository
  ) {
    this.noticeRepository = noticeRepository;
    this.partsRepository = partsRepository;
    this.noticePartsRepository = noticePartsRepository;
  }


  @Transactional
  public void add(AddNoticeForm addNoticeForm, Integer partsId) {

    Parts parts = this.partsRepository.getReferenceById(partsId);
    List<Notice> notices = new ArrayList<>();

    for(Integer noticeId : addNoticeForm.getNoticeIds()) {
      Notice notice = this.noticeRepository.getReferenceById(noticeId);
      notices.add(notice);
    }

    parts.setNotices(notices);
    this.partsRepository.save(parts);
  }

  
  public void delete(Integer partsId) {

    Parts parts = this.partsRepository.getReferenceById(partsId);
    List<NoticeParts> noticeParts = this.noticePartsRepository.findByParts(parts);

    for(NoticeParts notice : noticeParts) {
      this.noticePartsRepository.deleteById(notice.getId());
    }

  }

}
