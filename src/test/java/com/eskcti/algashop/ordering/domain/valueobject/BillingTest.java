package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class BillingTest {

  @Test
  void shouldCreateWithValidValues() {
    Billing billing = Billing.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .email(ValueObjectTestFixtures.validEmail())
        .address(ValueObjectTestFixtures.validAddress())
        .build();

    Assertions.assertThat(billing.fullName()).isEqualTo(ValueObjectTestFixtures.validFullName());
    Assertions.assertThat(billing.document()).isEqualTo(ValueObjectTestFixtures.validDocument());
    Assertions.assertThat(billing.phone()).isEqualTo(ValueObjectTestFixtures.validPhone());
    Assertions.assertThat(billing.email()).isEqualTo(ValueObjectTestFixtures.validEmail());
    Assertions.assertThat(billing.address()).isEqualTo(ValueObjectTestFixtures.validAddress());
  }

  @Test
  void shouldNotCreateWithNullFullName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Billing.builder()
            .fullName(null)
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .email(ValueObjectTestFixtures.validEmail())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullDocument() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Billing.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(null)
            .phone(ValueObjectTestFixtures.validPhone())
            .email(ValueObjectTestFixtures.validEmail())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullPhone() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Billing.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(null)
            .email(ValueObjectTestFixtures.validEmail())
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullEmail() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Billing.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .email(null)
            .address(ValueObjectTestFixtures.validAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullAddress() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Billing.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .email(ValueObjectTestFixtures.validEmail())
            .address(null)
            .build());
  }
}
