package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT;

class ShoppingCartItemIncompatibleProductExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();
    ProductId productId = new ProductId();
    ShoppingCartItemIncompatibleProductException exception =
        new ShoppingCartItemIncompatibleProductException(shoppingCartItemId, productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, shoppingCartItemId, productId));
  }
}
