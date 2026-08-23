package com.eskcti.algashop.ordering.infrastructure.adapters.out.notification.customer;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers.NotifyNewRegistrationInput;

class ForNotifyingCustomersFakeImplTest {

  private final ForNotifyingCustomersFakeImpl forNotifyingCustomers = new ForNotifyingCustomersFakeImpl();

  @Test
  void shouldNotifyNewRegistration() {
    var input = new NotifyNewRegistrationInput(
        UUID.randomUUID(),
        "John",
        "john@email.com");

    assertThatCode(() -> forNotifyingCustomers.notifyNewRegistration(input))
        .doesNotThrowAnyException();
  }
}
