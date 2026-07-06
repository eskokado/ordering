package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class ShippingInfoTest {

  @Test
  void shouldCreateWithValidValues() {
    ShippingInfo shippingInfo = ShippingInfo.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .address(ValueObjectTestFixtures.validAddress())
        .build();

    Assertions.assertThat(shippingInfo.fullName()).isEqualTo(ValueObjectTestFixtures.validFullName());
    Assertions.assertThat(shippingInfo.document()).isEqualTo(ValueObjectTestFixtures.validDocument());
    Assertions.assertThat(shippingInfo.phone()).isEqualTo(ValueObjectTestFixtures.validPhone());
    Assertions.assertThat(shippingInfo.address()).isEqualTo(ValueObjectTestFixtures.validAddress());
  }

  @Test
  void shouldNotCreateWithNullFullName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShippingInfo.builder()
            .fullName(null)
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullDocument() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShippingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(null)
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullPhone() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShippingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(null)
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullAddress() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ShippingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(null)
            .build());
  }
}
