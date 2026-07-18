package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.DomainException;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;

class ShoppingCartCantProceedToCheckoutExceptionTest {

  @Test
  void shouldCreateException() {
    ShoppingCartCantProceedToCheckoutException exception = new ShoppingCartCantProceedToCheckoutException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
