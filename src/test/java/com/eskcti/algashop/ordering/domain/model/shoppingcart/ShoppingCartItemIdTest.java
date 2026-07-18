package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;

import org.assertj.core.api.Assertions;

class ShoppingCartItemIdTest {

  @Test
  void shouldCreateWithProvidedUuid() {
    UUID uuid = UUID.randomUUID();
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId(uuid);

    Assertions.assertThat(shoppingCartItemId.value()).isEqualTo(uuid);
    Assertions.assertThat(shoppingCartItemId.toString()).isEqualTo(uuid.toString());
  }

  @Test
  void shouldCreateWithGeneratedUuid() {
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();

    Assertions.assertThat(shoppingCartItemId.value()).isNotNull();
  }

  @Test
  void shouldCreateWithString() {
    UUID uuid = UUID.randomUUID();
    String uuidString = uuid.toString();
    ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId(uuidString);

    Assertions.assertThat(shoppingCartItemId.value()).isEqualTo(uuid);
  }

  @Test
  void shouldNotCreateWithNullUuid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new ShoppingCartItemId((UUID) null));
  }
}
