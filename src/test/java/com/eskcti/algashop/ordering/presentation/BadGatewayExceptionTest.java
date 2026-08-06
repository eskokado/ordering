package com.eskcti.algashop.ordering.presentation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BadGatewayExceptionTest {

  @Test
  void shouldCreateExceptionWithoutArgs() {
    BadGatewayException exception = new BadGatewayException();

    Assertions.assertThat(exception).isInstanceOf(RuntimeException.class);
    Assertions.assertThat(exception.getMessage()).isNull();
    Assertions.assertThat(exception.getCause()).isNull();
  }

  @Test
  void shouldCreateExceptionWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("upstream failure");

    BadGatewayException exception = new BadGatewayException("Product Catalog API Bad Gateway", cause);

    Assertions.assertThat(exception.getMessage()).isEqualTo("Product Catalog API Bad Gateway");
    Assertions.assertThat(exception.getCause()).isSameAs(cause);
  }
}
