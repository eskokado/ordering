package com.eskcti.algashop.ordering.core.domain.model.shoppingcart;

import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {
  void adjustPrice(ProductId productId, Money updatedPrice);

  void changeAvailability(ProductId productId, boolean available);
}