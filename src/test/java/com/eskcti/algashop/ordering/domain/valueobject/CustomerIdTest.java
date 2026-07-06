package com.eskcti.algashop.ordering.domain.valueobject;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class CustomerIdTest {

  @Test
  void shouldCreateWithProvidedUuid() {
    UUID uuid = UUID.randomUUID();
    CustomerId customerId = new CustomerId(uuid);

    Assertions.assertThat(customerId.value()).isEqualTo(uuid);
    Assertions.assertThat(customerId.toString()).isEqualTo(uuid.toString());
  }

  @Test
  void shouldCreateWithGeneratedUuid() {
    CustomerId customerId = new CustomerId();

    Assertions.assertThat(customerId.value()).isNotNull();
  }

  @Test
  void shouldNotCreateWithNullUuid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new CustomerId(null));
  }
}
