package com.eskcti.algashop.ordering.infrastructure.notification.customer;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;

class CustomerNotificationServiceFakeImplTest {

  private final CustomerNotificationServiceFakeImpl customerNotificationService = new CustomerNotificationServiceFakeImpl();

  @Test
  void shouldNotifyNewRegistration() {
    var input = new NotifyNewRegistrationInput(
        UUID.randomUUID(),
        "John",
        "john@email.com");

    assertThatCode(() -> customerNotificationService.notifyNewRegistration(input))
        .doesNotThrowAnyException();
  }
}
