package com.eskcti.algashop.ordering.domain.entity;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.Product;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.eskcti.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.eskcti.algashop.ordering.domain.exception.ShoppingCartDoesNotContainProductException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

  @Test
  void given_startShopping_whenBuild_shouldInitializeDefaults() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();

    assertThat(cart.id()).isNotNull();
    assertThat(cart.customerId()).isNotNull();
    assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO);
    assertThat(cart.createdAt()).isNotNull();
    assertThat(cart.items()).isEmpty();
  }

  @Test
  void given_shoppingCart_whenAddItem_shouldAddItemAndRecalculateTotals() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    cart.addItem(product, quantity);

    assertThat(cart.items()).hasSize(1);
    assertThat(cart.totalAmount()).isEqualTo(new Money("6000.00"));
    assertThat(cart.totalItems()).isEqualTo(new Quantity(2));
  }

  @Test
  void given_shoppingCart_whenAddExistingProduct_shouldUpdateQuantity() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    Product product = ProductTestDataBuilder.aProduct().build();
    cart.addItem(product, new Quantity(2));
    assertThat(cart.totalItems()).isEqualTo(new Quantity(2));
    assertThat(cart.totalAmount()).isEqualTo(new Money("6000.00"));

    cart.addItem(product, new Quantity(3));

    assertThat(cart.items()).hasSize(1);
    assertThat(cart.totalItems()).isEqualTo(new Quantity(5));
    assertThat(cart.totalAmount()).isEqualTo(new Money("15000.00"));
  }

  @Test
  void given_shoppingCart_whenAddOutOfStockProduct_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    Product outOfStockProduct = ProductTestDataBuilder.aProductUnavailable().build();

    Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
        .isThrownBy(() -> cart.addItem(outOfStockProduct, new Quantity(1)));
  }

  @Test
  void given_shoppingCart_whenAddItemWithNullProduct_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.addItem(null, new Quantity(1)));
  }

  @Test
  void given_shoppingCart_whenAddItemWithNullQuantity_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    Product product = ProductTestDataBuilder.aProduct().build();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.addItem(product, null));
  }

  @Test
  void given_shoppingCartWithItems_whenEmpty_shouldClearItemsAndTotals() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();

    cart.empty();

    assertThat(cart.items()).isEmpty();
    assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO);
  }

  @Test
  void given_shoppingCartWithItems_whenRemoveItem_shouldRemoveAndRecalculateTotals() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();
    Quantity initialTotalItems = cart.totalItems();
    Quantity itemQuantity = item.quantity();

    cart.removeItem(item.id());

    assertThat(cart.items()).isEmpty();
    assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(cart.totalItems()).isEqualTo(new Quantity(initialTotalItems.value() - itemQuantity.value()));
  }

  @Test
  void given_shoppingCart_whenRemoveNonExistentItem_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    ShoppingCartItemId nonExistentId = new ShoppingCartItemId();

    Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
        .isThrownBy(() -> cart.removeItem(nonExistentId));
  }

  @Test
  void given_shoppingCart_whenRemoveItemWithNullId_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.removeItem(null));
  }

  @Test
  void given_shoppingCartWithItem_whenChangeItemQuantity_shouldUpdateQuantityAndRecalculateTotals() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();
    Quantity newQuantity = new Quantity(5);

    cart.changeItemQuantity(item.id(), newQuantity);

    assertThat(item.quantity()).isEqualTo(newQuantity);
    assertThat(cart.totalItems()).isEqualTo(newQuantity);
  }

  @Test
  void given_shoppingCart_whenChangeNonExistentItem_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    ShoppingCartItemId nonExistentId = new ShoppingCartItemId();

    Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
        .isThrownBy(() -> cart.changeItemQuantity(nonExistentId, new Quantity(1)));
  }

  @Test
  void given_shoppingCart_whenChangeItemQuantityWithNullId_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.changeItemQuantity(null, new Quantity(1)));
  }

  @Test
  void given_shoppingCart_whenChangeItemQuantityWithNullQuantity_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.changeItemQuantity(item.id(), null));
  }

  @Test
  void given_shoppingCartWithItem_whenFindItemByItemId_shouldReturnItem() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();

    ShoppingCartItem foundItem = cart.findItem(item.id());

    assertThat(foundItem).isEqualTo(item);
  }

  @Test
  void given_shoppingCartWithItem_whenFindItemByProductId_shouldReturnItem() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();

    ShoppingCartItem foundItem = cart.findItem(item.productId());

    assertThat(foundItem).isEqualTo(item);
  }

  @Test
  void given_shoppingCart_whenFindItemWithNullProductId_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.findItem((ProductId) null));
  }

  @Test
  void given_shoppingCart_whenFindItemWithNullItemId_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> cart.findItem((ShoppingCartItemId) null));
  }

  @Test
  void given_shoppingCartWithoutProduct_whenFindByProductId_shouldGenerateException() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    ProductId nonExistentProductId = new ProductId();

    Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainProductException.class)
        .isThrownBy(() -> cart.findItem(nonExistentProductId));
  }

  @Test
  void given_shoppingCartWithItem_whenRefreshItem_shouldUpdateItemFields() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    ShoppingCartItem item = cart.items().iterator().next();
    Product altProduct = ProductTestDataBuilder.aProductAltRamMemory().build();
    Product updatedProduct = Product.builder()
        .id(item.productId())
        .name(altProduct.name())
        .price(new Money("4000.00"))
        .inStock(false)
        .build();

    cart.refreshItem(updatedProduct);

    ShoppingCartItem refreshedItem = cart.findItem(item.id());
    assertThat(refreshedItem.name()).isEqualTo(updatedProduct.name());
    assertThat(refreshedItem.price()).isEqualTo(updatedProduct.price());
    assertThat(refreshedItem.isAvailable()).isEqualTo(updatedProduct.inStock());
  }

  @Test
  void given_shoppingCartWithAvailableItems_whenCheckUnavailable_shouldReturnFalse() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    assertThat(cart.containsUnavailableItems()).isFalse();
  }

  @Test
  void given_shoppingCartWithUnavailableItems_whenCheckUnavailable_shouldReturnTrue() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCartWithUnavailableItems();
    assertThat(cart.containsUnavailableItems()).isTrue();
  }

  @Test
  void given_emptyShoppingCart_whenCheckIsEmpty_shouldReturnTrue() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();
    assertThat(cart.isEmpty()).isTrue();
  }

  @Test
  void given_shoppingCartWithItems_whenCheckIsEmpty_shouldReturnFalse() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart();
    assertThat(cart.isEmpty()).isFalse();
  }

  @Test
  void given_sameShoppingCartInstance_whenCompare_shouldBeEqual() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.startShopping();

    assertThat(cart).isEqualTo(cart);
  }

  @Test
  void given_shoppingCartsWithSameId_whenCompare_shouldBeEqual() {
    ShoppingCartId id = new ShoppingCartId();
    CustomerId customerId = new CustomerId();
    ShoppingCart first = ShoppingCart.existing()
        .id(id)
        .customerId(customerId)
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .createdAt(java.time.OffsetDateTime.now())
        .items(Set.of())
        .build();
    ShoppingCart second = ShoppingCart.existing()
        .id(id)
        .customerId(new CustomerId())
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .createdAt(java.time.OffsetDateTime.now())
        .items(Set.of())
        .build();

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_shoppingCartsWithDifferentId_whenCompare_shouldNotBeEqual() {
    ShoppingCart first = ShoppingCartTestDataBuilder.startShopping();
    ShoppingCart second = ShoppingCartTestDataBuilder.startShopping();

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-a-shopping-cart");
  }

  @Test
  void given_nullRequiredFields_whenBuildExistingShoppingCart_shouldGenerateException() {
    CustomerId customerId = new CustomerId();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(null)
            .customerId(customerId)
            .totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO)
            .createdAt(java.time.OffsetDateTime.now())
            .items(Set.of())
            .build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(new ShoppingCartId())
            .customerId(null)
            .totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO)
            .createdAt(java.time.OffsetDateTime.now())
            .items(Set.of())
            .build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(new ShoppingCartId())
            .customerId(customerId)
            .totalAmount(null)
            .totalItems(Quantity.ZERO)
            .createdAt(java.time.OffsetDateTime.now())
            .items(Set.of())
            .build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(new ShoppingCartId())
            .customerId(customerId)
            .totalAmount(Money.ZERO)
            .totalItems(null)
            .createdAt(java.time.OffsetDateTime.now())
            .items(Set.of())
            .build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(new ShoppingCartId())
            .customerId(customerId)
            .totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO)
            .createdAt(null)
            .items(Set.of())
            .build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.existing()
            .id(new ShoppingCartId())
            .customerId(customerId)
            .totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO)
            .createdAt(java.time.OffsetDateTime.now())
            .items(null)
            .build());
  }

  @Test
  void given_nullCustomerId_whenStartShopping_shouldGenerateException() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShoppingCart.startShopping(null));
  }
}
