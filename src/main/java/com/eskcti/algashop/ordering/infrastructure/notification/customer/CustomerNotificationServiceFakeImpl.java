package com.eskcti.algashop.ordering.infrastructure.notification.customer;

import org.springframework.stereotype.Service;

import com.eskcti.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationApplicationService {

  @Override
  public void notifyNewRegistration(NotifyNewRegistrationInput input) {
    log.info("Welcome {}", input.firstName());
    log.info("User your email to access your account {}", input.email());
  }
}