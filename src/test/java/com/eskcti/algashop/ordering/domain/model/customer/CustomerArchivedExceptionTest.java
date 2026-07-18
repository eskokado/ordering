package com.eskcti.algashop.ordering.domain.model.customer;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedException;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_ARCHIVED;

import org.assertj.core.api.Assertions;

class CustomerArchivedExceptionTest {

  @Test
  void shouldCreateWithDefaultMessage() {
    CustomerArchivedException exception = new CustomerArchivedException();

    Assertions.assertThat(exception.getMessage()).isEqualTo(ERROR_CUSTOMER_ARCHIVED);
  }

  @Test
  void shouldCreateWithCause() {
    RuntimeException cause = new RuntimeException("cause");
    CustomerArchivedException exception = new CustomerArchivedException(cause);

    Assertions.assertThat(exception.getMessage()).isEqualTo(ERROR_CUSTOMER_ARCHIVED);
    Assertions.assertThat(exception.getCause()).isEqualTo(cause);
  }
}
