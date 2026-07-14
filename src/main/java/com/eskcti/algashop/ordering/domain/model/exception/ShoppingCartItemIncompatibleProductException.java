package com.eskcti.algashop.ordering.domain.model.exception;

import com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
  public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId id, ProductId productId) {
    super(String.format(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, id, productId));
  }
}
