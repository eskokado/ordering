package com.eskcti.algashop.ordering.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;

import org.assertj.core.api.Assertions;

class ZipCodeTest {

  @Test
  void shouldCreateWithValidValue() {
    ZipCode zipCode = new ZipCode("12345");

    Assertions.assertThat(zipCode.value()).isEqualTo("12345");
    Assertions.assertThat(zipCode.toString()).isEqualTo("12345");
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new ZipCode(null));
  }

  @Test
  void shouldNotCreateWithBlankValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new ZipCode(" "));
  }

  @Test
  void shouldNotCreateWithInvalidLength() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new ZipCode("1234"));

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new ZipCode("123456"));
  }
}
