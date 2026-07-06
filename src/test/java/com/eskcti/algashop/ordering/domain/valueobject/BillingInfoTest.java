package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class BillingInfoTest {

  @Test
  void shouldCreateWithValidValues() {
    BillingInfo billingInfo = BillingInfo.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .address(ValueObjectTestFixtures.validAddress())
        .build();

    Assertions.assertThat(billingInfo.fullName()).isEqualTo(ValueObjectTestFixtures.validFullName());
    Assertions.assertThat(billingInfo.document()).isEqualTo(ValueObjectTestFixtures.validDocument());
    Assertions.assertThat(billingInfo.phone()).isEqualTo(ValueObjectTestFixtures.validPhone());
    Assertions.assertThat(billingInfo.address()).isEqualTo(ValueObjectTestFixtures.validAddress());
  }

  @Test
  void shouldNotCreateWithNullFullName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> BillingInfo.builder()
            .fullName(null)
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullDocument() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> BillingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(null)
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullPhone() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> BillingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(null)
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullAddress() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> BillingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(null)
            .build());
  }
}
