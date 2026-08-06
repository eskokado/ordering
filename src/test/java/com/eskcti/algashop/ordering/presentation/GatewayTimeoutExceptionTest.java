package com.eskcti.algashop.ordering.presentation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GatewayTimeoutExceptionTest {

  @Test
  void shouldCreateExceptionWithoutArgs() {
    GatewayTimeoutException exception = new GatewayTimeoutException();

    Assertions.assertThat(exception).isInstanceOf(RuntimeException.class);
    Assertions.assertThat(exception.getMessage()).isNull();
    Assertions.assertThat(exception.getCause()).isNull();
  }

  @Test
  void shouldCreateExceptionWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("connection timed out");

    GatewayTimeoutException exception = new GatewayTimeoutException("Product Catalog API Timeout", cause);

    Assertions.assertThat(exception.getMessage()).isEqualTo("Product Catalog API Timeout");
    Assertions.assertThat(exception.getCause()).isSameAs(cause);
  }
}
