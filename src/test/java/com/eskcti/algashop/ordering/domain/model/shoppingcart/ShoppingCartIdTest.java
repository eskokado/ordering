package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

import org.assertj.core.api.Assertions;

class ShoppingCartIdTest {

  @Test
  void shouldCreateWithProvidedUuid() {
    UUID uuid = UUID.randomUUID();
    ShoppingCartId shoppingCartId = new ShoppingCartId(uuid);

    Assertions.assertThat(shoppingCartId.value()).isEqualTo(uuid);
    Assertions.assertThat(shoppingCartId.toString()).isEqualTo(uuid.toString());
  }

  @Test
  void shouldCreateWithGeneratedUuid() {
    ShoppingCartId shoppingCartId = new ShoppingCartId();

    Assertions.assertThat(shoppingCartId.value()).isNotNull();
  }

  @Test
  void shouldCreateWithString() {
    UUID uuid = UUID.randomUUID();
    String uuidString = uuid.toString();
    ShoppingCartId shoppingCartId = new ShoppingCartId(uuidString);

    Assertions.assertThat(shoppingCartId.value()).isEqualTo(uuid);
  }

  @Test
  void shouldNotCreateWithNullUuid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new ShoppingCartId((UUID) null));
  }
}
