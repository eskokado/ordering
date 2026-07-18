package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.time.OffsetDateTime;
import java.util.Set;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;

public final class ShoppingCartTestDataBuilder {

  private CustomerId customerId = new CustomerId();
  private boolean withItems = true;

  private ShoppingCartTestDataBuilder() {
  }

  public static ShoppingCartTestDataBuilder aShoppingCart() {
    return new ShoppingCartTestDataBuilder();
  }

  public static ShoppingCart startShopping() {
    return ShoppingCart.startShopping(new CustomerId());
  }

  public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
    this.customerId = customerId;
    return this;
  }

  public ShoppingCartTestDataBuilder withItems(boolean withItems) {
    this.withItems = withItems;
    return this;
  }

  public ShoppingCart build() {
    ShoppingCart cart = ShoppingCart.startShopping(customerId);

    if (withItems) {
      cart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
    }

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
        .version(null)
        .customerId(customerId)
        .totalAmount(totalAmount)
        .totalItems(quantity)
        .createdAt(OffsetDateTime.now())
        .items(Set.of(unavailableItem))
        .build();
  }
}
