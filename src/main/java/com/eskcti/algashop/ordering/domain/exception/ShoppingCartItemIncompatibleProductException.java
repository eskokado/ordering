package com.eskcti.algashop.ordering.domain.exception;

import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.exception.ErrorMessages;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
  public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId id, ProductId productId) {
    super(String.format(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, id, productId));
  }
}
