package com.eskcti.algashop.ordering.domain.exception;

import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import com.eskcti.algashop.ordering.domain.exception.ErrorMessages;

public class ShoppingCartDoesNotContainItemException extends DomainException {
  public ShoppingCartDoesNotContainItemException(ShoppingCartId id, ShoppingCartItemId shoppingCartItemId) {
    super(String.format(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, id, shoppingCartItemId));
  }
}