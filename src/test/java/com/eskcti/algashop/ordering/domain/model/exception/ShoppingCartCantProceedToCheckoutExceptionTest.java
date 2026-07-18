package com.eskcti.algashop.ordering.domain.model.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartCantProceedToCheckoutExceptionTest {

  @Test
  void shouldCreateException() {
    ShoppingCartCantProceedToCheckoutException exception = new ShoppingCartCantProceedToCheckoutException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
