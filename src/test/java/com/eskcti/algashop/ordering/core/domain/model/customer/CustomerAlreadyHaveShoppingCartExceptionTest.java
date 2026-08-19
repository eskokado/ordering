package com.eskcti.algashop.ordering.core.domain.model.customer;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;

class CustomerAlreadyHaveShoppingCartExceptionTest {

  @Test
  void shouldCreateException() {

    CustomerAlreadyHaveShoppingCartException exception = new CustomerAlreadyHaveShoppingCartException(
        new CustomerId(UUID.randomUUID()));

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
