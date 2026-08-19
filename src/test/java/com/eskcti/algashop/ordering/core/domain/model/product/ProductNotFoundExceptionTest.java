package com.eskcti.algashop.ordering.core.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductNotFoundException;

class ProductNotFoundExceptionTest {

  @Test
  void shouldCreateException() {
    ProductNotFoundException exception = new ProductNotFoundException();

    Assertions.assertThat(exception).isInstanceOf(DomainEntityNotFoundException.class);
  }
}
