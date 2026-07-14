package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;

class ProductOutOfStockExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ProductId productId = new ProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    ProductOutOfStockException exception = new ProductOutOfStockException(productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, productId));
  }
}
