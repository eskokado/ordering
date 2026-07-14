package com.eskcti.algashop.ordering.domain.model.exception;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.exception.DomainException;

import org.assertj.core.api.Assertions;

class DomainExceptionTest {

  @Test
  void shouldCreateWithMessage() {
    DomainException exception = new DomainException("domain error");

    Assertions.assertThat(exception.getMessage()).isEqualTo("domain error");
  }

  @Test
  void shouldCreateWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("cause");
    DomainException exception = new DomainException("domain error", cause);

    Assertions.assertThat(exception.getMessage()).isEqualTo("domain error");
    Assertions.assertThat(exception.getCause()).isEqualTo(cause);
  }
}
