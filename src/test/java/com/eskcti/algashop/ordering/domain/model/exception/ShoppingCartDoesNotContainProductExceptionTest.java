package com.eskcti.algashop.ordering.domain.model.exception;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainProductException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

class ShoppingCartDoesNotContainProductExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ShoppingCartId shoppingCartId = new ShoppingCartId();
    ProductId productId = new ProductId();
    ShoppingCartDoesNotContainProductException exception = new ShoppingCartDoesNotContainProductException(
        shoppingCartId, productId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, shoppingCartId, productId));
  }
}
