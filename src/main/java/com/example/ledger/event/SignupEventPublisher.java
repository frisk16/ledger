package com.example.ledger.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.ledger.entity.User;

@Component
public class SignupEventPublisher {
  
  private final ApplicationEventPublisher applicationEventPublisher;

  public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishSignupEvent(User user, String requestUrl) {
    this.applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
  }

}
