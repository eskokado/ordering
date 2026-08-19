package com.eskcti.algashop.ordering.core.domain.model.customer;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerEmailIsInUseException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;

class CustomerEmailIsInUseExceptionTest {
  @Test
  void shouldCreateException() {
    CustomerEmailIsInUseException exception = new CustomerEmailIsInUseException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldCreateExceptionWithCustomerId() {
    CustomerEmailIsInUseException exception = new CustomerEmailIsInUseException(new CustomerId(UUID.randomUUID()));

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
