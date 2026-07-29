package com.eskcti.algashop.ordering.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DomainEntityNotFoundExceptionTest {

  @Test
  void shouldCreateWithoutArguments() {
    DomainEntityNotFoundException exception = new DomainEntityNotFoundException();

    Assertions.assertThat(exception.getMessage()).isNull();
    Assertions.assertThat(exception.getCause()).isNull();
  }

  @Test
  void shouldCreateWithCause() {
    RuntimeException cause = new RuntimeException("cause");
    DomainEntityNotFoundException exception = new DomainEntityNotFoundException(cause);

    Assertions.assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  void shouldCreateWithMessage() {
    DomainEntityNotFoundException exception = new DomainEntityNotFoundException("entity not found");

    Assertions.assertThat(exception.getMessage()).isEqualTo("entity not found");
  }

  @Test
  void shouldCreateWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("cause");
    DomainEntityNotFoundException exception = new DomainEntityNotFoundException("entity not found", cause);

    Assertions.assertThat(exception.getMessage()).isEqualTo("entity not found");
    Assertions.assertThat(exception.getCause()).isEqualTo(cause);
  }
}
