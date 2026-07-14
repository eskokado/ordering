package com.eskcti.algashop.ordering.domain.model.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;

import java.util.UUID;

class ProductOutOfStockExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ProductId productId = new ProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    ProductOutOfStockException exception = new ProductOutOfStockException(productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, productId));
  }
}
