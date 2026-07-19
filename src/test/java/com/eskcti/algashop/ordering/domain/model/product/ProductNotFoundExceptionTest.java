package com.eskcti.algashop.ordering.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.DomainException;

class ProductNotFoundExceptionTest {

  @Test
  void shouldCreateException() {
    ProductNotFoundException exception = new ProductNotFoundException();

    Assertions.assertThat(exception).isInstanceOf(DomainException.class);
  }
}
