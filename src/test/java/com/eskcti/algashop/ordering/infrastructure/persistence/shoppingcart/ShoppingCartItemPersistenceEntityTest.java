package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;

class ShoppingCartItemPersistenceEntityTest {

  @Test
  void givenShoppingCartItemWithShoppingCart_whenGetShoppingCartId_shouldReturnShoppingCartId() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();
    ShoppingCartItemPersistenceEntity item = ShoppingCartPersistenceEntityTestDataBuilder.existingItem()
        .shoppingCart(cart)
        .build();

    assertThat(item.getShoppingCartId()).isEqualTo(cart.getId());
  }

  @Test
  void givenShoppingCartItemWithoutShoppingCart_whenGetShoppingCartId_shouldReturnNull() {
    ShoppingCartItemPersistenceEntity item = new ShoppingCartItemPersistenceEntity();

    assertThat(item.getShoppingCartId()).isNull();
  }
}
