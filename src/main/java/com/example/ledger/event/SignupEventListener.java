package com.example.ledger.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.ledger.entity.User;
import com.example.ledger.service.VerificationTokenService;

@Component
public class SignupEventListener {

  private final VerificationTokenService verificationTokenService;
  private final JavaMailSender javaMailSender;

  public SignupEventListener(
    VerificationTokenService verificationTokenService,
    JavaMailSender javaMailSender
  ) {
    this.verificationTokenService = verificationTokenService;
    this.javaMailSender = javaMailSender;
  }

  @EventListener
  private void onSignupEvent(SignupEvent signupEvent) {
    User user = signupEvent.getUser();
    String token = UUID.randomUUID().toString();
    this.verificationTokenService.create(user, token);

    String destinationAddress = user.getEmail();
    String subject = "【帳簿管理システム】メール送信エラー";
    String confirmationUrl = signupEvent.getRequestUrl();
    String message = "メールが正しく送信されませんでした、お手数ですがもう一度設定をお願いします。";
    
    // 会員登録
    if(confirmationUrl.endsWith("register")) {
      subject = "【帳簿管理システム】登録完了メール";
      confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
      message = "以下のリンクをクリックして登録を完了してください。";
    }

    // パスワード再設定
    if(confirmationUrl.endsWith("resetPassword")) {
      subject = "【帳簿管理システム】パスワード再設定メール";
      confirmationUrl = signupEvent.getRequestUrl() + "/setting?token=" + token;
      message = "以下のリンクをクリックしてパスワードの再設定を行ってください。";
    }

    SimpleMailMessage mail = new SimpleMailMessage();
    mail.setTo(destinationAddress);
    mail.setSubject(subject);
    mail.setText(message + "\n" + confirmationUrl);
    this.javaMailSender.send(mail);
  }
}
