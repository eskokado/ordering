package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class RecipientTest {

  @Test
  void shouldCreateWithValidValues() {
    Recipient recipient = Recipient.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .build();

    Assertions.assertThat(recipient.fullName()).isEqualTo(ValueObjectTestFixtures.validFullName());
    Assertions.assertThat(recipient.document()).isEqualTo(ValueObjectTestFixtures.validDocument());
    Assertions.assertThat(recipient.phone()).isEqualTo(ValueObjectTestFixtures.validPhone());
  }

  @Test
  void shouldNotCreateWithNullFullName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Recipient.builder()
            .fullName(null)
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .build());
  }

  @Test
  void shouldNotCreateWithNullDocument() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Recipient.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(null)
            .phone(ValueObjectTestFixtures.validPhone())
            .build());
  }

  @Test
  void shouldNotCreateWithNullPhone() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Recipient.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(null)
            .build());
  }
}
