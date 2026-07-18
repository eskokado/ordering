package com.eskcti.algashop.ordering.domain.model.product;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.product.ProductId;

import org.assertj.core.api.Assertions;

class ProductIdTest {

  @Test
  void shouldCreateWithProvidedUuid() {
    UUID uuid = UUID.randomUUID();
    ProductId productId = new ProductId(uuid);

    Assertions.assertThat(productId.value()).isEqualTo(uuid);
    Assertions.assertThat(productId.toString()).isEqualTo(uuid.toString());
  }

  @Test
  void shouldCreateWithGeneratedUuid() {
    ProductId productId = new ProductId();

    Assertions.assertThat(productId.value()).isNotNull();
  }

  @Test
  void shouldNotCreateWithNullUuid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new ProductId(null));
  }
}
