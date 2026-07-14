package com.eskcti.algashop.ordering.domain.model.entity;

import java.time.OffsetDateTime;
import java.util.Set;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Product;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

public final class ShoppingCartTestDataBuilder {
  private ShoppingCartTestDataBuilder() {
  }

  public static ShoppingCart startShopping() {
    return ShoppingCart.startShopping(new CustomerId());
  }

  public static ShoppingCart aShoppingCart() {
    ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
    cart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
    return cart;
  }

  public static ShoppingCart aShoppingCartWithUnavailableItems() {
    ShoppingCartId cartId = new ShoppingCartId();
    CustomerId customerId = new CustomerId();
    ProductId productId = new ProductId();
    ProductName productName = new ProductName("Unavailable Product");
    Money price = new Money("100.00");
    Quantity quantity = new Quantity(1);
    Money totalAmount = price.multiply(quantity);

    ShoppingCartItem unavailableItem = ShoppingCartItem.existing()
        .id(new ShoppingCartItemId())
        .shoppingCartId(cartId)
        .productId(productId)
        .productName(productName)
        .price(price)
        .quantity(quantity)
        .available(false)
        .totalAmount(totalAmount)
        .build();

    return ShoppingCart.existing()
        .id(cartId)
        .customerId(customerId)
        .totalAmount(totalAmount)
        .totalItems(quantity)
        .createdAt(OffsetDateTime.now())
        .items(Set.of(unavailableItem))
        .build();
  }
}
