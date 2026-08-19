package com.eskcti.algashop.ordering.core.domain.model.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;

class CustomerNotFoundExceptionTest {

  @Test
  void shouldCreateException() {
    CustomerNotFoundException exception = new CustomerNotFoundException();

    Assertions.assertThat(exception).isInstanceOf(DomainEntityNotFoundException.class);
  }
}
