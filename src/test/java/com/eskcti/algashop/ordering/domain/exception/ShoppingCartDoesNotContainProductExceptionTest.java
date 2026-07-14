package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

class ShoppingCartDoesNotContainProductExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ShoppingCartId shoppingCartId = new ShoppingCartId();
    ProductId productId = new ProductId();
    ShoppingCartDoesNotContainProductException exception =
        new ShoppingCartDoesNotContainProductException(shoppingCartId, productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, shoppingCartId, productId));
  }
}
