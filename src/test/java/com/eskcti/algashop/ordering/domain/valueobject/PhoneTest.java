package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class PhoneTest {

  @Test
  void shouldCreateWithValidValue() {
    Phone phone = new Phone("478-256-2604");

    Assertions.assertThat(phone.value()).isEqualTo("478-256-2604");
    Assertions.assertThat(phone.toString()).isEqualTo("478-256-2604");
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Phone(null));
  }

  @Test
  void shouldNotCreateWithBlankValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Phone(" "));
  }
}
