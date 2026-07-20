package com.eskcti.algashop.ordering.infrastructure.listener.customer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerEventListener {

  @EventListener
  public void listen(CustomerRegisteredEvent event) {
    log.info("CustomerRegisteredEvent listen 1");
  }

  @EventListener
  public void listenSecondary(CustomerRegisteredEvent event) {
    log.info("CustomerRegisteredEvent listen 2");
  }

  @EventListener
  public void listen(CustomerArchivedEvent event) {
    log.info("CustomerArchivedEvent listen 1");
  }

}
