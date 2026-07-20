package com.eskcti.algashop.ordering.infrastructure.listener.customer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

  private final CustomerNotificationService customerNotificationService;

  @EventListener
  public void listen(CustomerRegisteredEvent event) {
    log.info("CustomerRegisteredEvent listen 1");
    customerNotificationService.notifyNewRegistration(event.customerId().value());
  }

  @EventListener
  public void listen(CustomerArchivedEvent event) {
    log.info("CustomerArchivedEvent listen 1");
  }

}
