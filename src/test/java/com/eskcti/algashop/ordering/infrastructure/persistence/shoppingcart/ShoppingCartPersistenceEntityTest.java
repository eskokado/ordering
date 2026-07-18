package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;

class ShoppingCartPersistenceEntityTest {

  @Test
  void givenShoppingCartWithCustomer_whenGetCustomerId_shouldReturnCustomerId() {
    CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart()
        .customer(customer)
        .build();

    assertThat(cart.getCustomerId()).isEqualTo(customer.getId());
  }

  @Test
  void givenShoppingCartWithoutCustomer_whenGetCustomerId_shouldReturnNull() {
    ShoppingCartPersistenceEntity cart = new ShoppingCartPersistenceEntity();

    assertThat(cart.getCustomerId()).isNull();
  }

  @Test
  void givenShoppingCart_whenAddItem_shouldSetShoppingCartReference() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();
    ShoppingCartItemPersistenceEntity item = ShoppingCartPersistenceEntityTestDataBuilder.existingItem().build();

    cart.addItem(item);

    assertThat(cart.getItems()).contains(item);
    assertThat(item.getShoppingCartId()).isEqualTo(cart.getId());
  }

  @Test
  void givenShoppingCart_whenAddNullItem_shouldDoNothing() {
    ShoppingCartPersistenceEntity cart = new ShoppingCartPersistenceEntity();

    cart.addItem((ShoppingCartItemPersistenceEntity) null);

    assertThat(cart.getItems()).isEmpty();
  }

  @Test
  void givenShoppingCartWithNullItems_whenAddItem_shouldInitializeItems() {
    ShoppingCartPersistenceEntity cart = new ShoppingCartPersistenceEntity();
    cart.setItems(null);
    ShoppingCartItemPersistenceEntity item = ShoppingCartPersistenceEntityTestDataBuilder.existingItem().build();

    cart.addItem(item);

    assertThat(cart.getItems()).isNotEmpty();
  }

  @Test
  void givenShoppingCart_whenReplaceItems_shouldSetAllItemsWithShoppingCartReference() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();
    ShoppingCartItemPersistenceEntity item1 = ShoppingCartPersistenceEntityTestDataBuilder.existingItem().build();
    ShoppingCartItemPersistenceEntity item2 = ShoppingCartPersistenceEntityTestDataBuilder.existingItemAlt().build();

    cart.replaceItems(Set.of(item1, item2));

    assertThat(cart.getItems()).containsAll(Set.of(item1, item2));
    assertThat(item1.getShoppingCartId()).isEqualTo(cart.getId());
    assertThat(item2.getShoppingCartId()).isEqualTo(cart.getId());
  }

  @Test
  void givenShoppingCart_whenReplaceWithNullItems_shouldInitializeEmptyItems() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();

    cart.replaceItems(null);

    assertThat(cart.getItems()).isEmpty();
  }

  @Test
  void givenShoppingCart_whenReplaceWithEmptyItems_shouldInitializeEmptyItems() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();

    cart.replaceItems(Set.of());

    assertThat(cart.getItems()).isEmpty();
  }

  @Test
  void givenShoppingCart_whenAddItems_shouldAddAllItemsWithShoppingCartReference() {
    ShoppingCartPersistenceEntity cart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();
    ShoppingCartItemPersistenceEntity item1 = ShoppingCartPersistenceEntityTestDataBuilder.existingItem().build();
    ShoppingCartItemPersistenceEntity item2 = ShoppingCartPersistenceEntityTestDataBuilder.existingItemAlt().build();

    cart.addItem(Set.of(item1, item2));

    assertThat(cart.getItems()).containsAll(Set.of(item1, item2));
  }
}
