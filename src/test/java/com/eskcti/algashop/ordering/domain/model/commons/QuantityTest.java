package com.eskcti.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Quantity;

import org.assertj.core.api.Assertions;

class QuantityTest {

  @Test
  void shouldCreateWithValidValue() {
    Quantity quantity = new Quantity(5);

    Assertions.assertThat(quantity.value()).isEqualTo(5);
    Assertions.assertThat(quantity.toString()).isEqualTo("5");
  }

  @Test
  void shouldExposeZeroConstant() {
    Assertions.assertThat(Quantity.ZERO.value()).isZero();
  }

  @Test
  void shouldAddValues() {
    Quantity result = new Quantity(5).add(new Quantity(3));

    Assertions.assertThat(result.value()).isEqualTo(8);
  }

  @Test
  void shouldCompareValues() {
    Quantity lower = new Quantity(1);
    Quantity higher = new Quantity(2);

    Assertions.assertThat(lower.compareTo(higher)).isNegative();
    Assertions.assertThat(higher.compareTo(lower)).isPositive();
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Quantity(null));
  }

  @Test
  void shouldNotCreateWithNegativeValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Quantity(-1));
  }

  @Test
  void shouldNotAddNullQuantity() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Quantity(1).add(null));
  }
}
