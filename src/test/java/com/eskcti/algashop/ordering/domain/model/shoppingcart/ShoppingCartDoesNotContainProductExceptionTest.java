package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartDoesNotContainProductException;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

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
