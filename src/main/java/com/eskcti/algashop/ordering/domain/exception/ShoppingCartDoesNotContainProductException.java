package com.eskcti.algashop.ordering.domain.exception;

import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.exception.ErrorMessages;

public class ShoppingCartDoesNotContainProductException extends DomainException {
  public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
    super(String.format(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, id, productId));
  }
}
