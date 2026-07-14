package com.eskcti.algashop.ordering.domain.model.exception;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainItemException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

class ShoppingCartDoesNotContainItemExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    ShoppingCartId shoppingCartId = new ShoppingCartId();
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();
    ShoppingCartDoesNotContainItemException exception = new ShoppingCartDoesNotContainItemException(shoppingCartId,
        shoppingCartItemId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, shoppingCartId, shoppingCartItemId));
  }
}
