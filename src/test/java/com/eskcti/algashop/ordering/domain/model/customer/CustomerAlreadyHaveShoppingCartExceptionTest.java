package com.eskcti.algashop.ordering.domain.model.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.DomainException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;

class CustomerAlreadyHaveShoppingCartExceptionTest {

  @Test
  void shouldCreateException() {
    CustomerAlreadyHaveShoppingCartException exception = new CustomerAlreadyHaveShoppingCartException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
