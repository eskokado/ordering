package com.eskcti.algashop.ordering.domain.exception;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_CUSTOMER_ARCHIVED;

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
