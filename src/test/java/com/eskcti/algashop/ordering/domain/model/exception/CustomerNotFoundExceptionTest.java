package com.eskcti.algashop.ordering.domain.model.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerNotFoundExceptionTest {

  @Test
  void shouldCreateException() {
    CustomerNotFoundException exception = new CustomerNotFoundException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
