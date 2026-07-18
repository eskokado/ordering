package com.eskcti.algashop.ordering.domain.model.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.DomainException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;

class CustomerNotFoundExceptionTest {

  @Test
  void shouldCreateException() {
    CustomerNotFoundException exception = new CustomerNotFoundException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
