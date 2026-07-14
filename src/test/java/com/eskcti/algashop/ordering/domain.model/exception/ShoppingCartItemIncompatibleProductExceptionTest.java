package com.eskcti.algashop.ordering.domain.model.exception;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

class ShoppingCartItemIncompatibleProductExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();
    ProductId productId = new ProductId();
    ShoppingCartItemIncompatibleProductException exception = new ShoppingCartItemIncompatibleProductException(
        shoppingCartItemId, productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, shoppingCartItemId, productId));
  }
}
