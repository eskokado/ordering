package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;

public final class ShoppingCartItemTestDataBuilder {
  private ShoppingCartItemTestDataBuilder() {
  }

  public static ShoppingCartItem.BrandNewShoppingCartItem brandNewItem(ShoppingCartId shoppingCartId) {
    return ShoppingCartItem.brandNew()
        .shoppingCartId(shoppingCartId)
        .productId(validProductId())
        .productName(validProductName())
        .price(validPrice())
        .quantity(validQuantity())
        .available(true);
  }

  public static ShoppingCartItem.ExistingShoppingCartItem existingItem(ShoppingCartId shoppingCartId) {
    return ShoppingCartItem.existing()
        .id(new ShoppingCartItemId())
        .shoppingCartId(shoppingCartId)
        .productId(validProductId())
        .productName(validProductName())
        .price(validPrice())
        .quantity(validQuantity())
        .available(true)
        .totalAmount(new Money("100.00"));
  }

  public static ProductId validProductId() {
    return new ProductId();
  }

  public static ProductName validProductName() {
    return new ProductName("Notebook");
  }

  public static Money validPrice() {
    return new Money("50.00");
  }

  public static Quantity validQuantity() {
    return new Quantity(2);
  }
}
