package com.eskcti.algashop.ordering.domain.model.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerAlreadyHaveShoppingCartExceptionTest {

  @Test
  void shouldCreateException() {
    CustomerAlreadyHaveShoppingCartException exception = new CustomerAlreadyHaveShoppingCartException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
