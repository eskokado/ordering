package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class LoyaltyPointsTest {

  @Test
  void shouldGenerateWithValue() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
    Assertions.assertThat(loyaltyPoints.value()).isEqualTo(10);
  }

  @Test
  void shouldExposeZeroConstant() {
    Assertions.assertThat(LoyaltyPoints.ZERO.value()).isZero();
  }

  @Test
  void shouldCreateWithDefaultConstructor() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
    Assertions.assertThat(loyaltyPoints.value()).isZero();
  }

  @Test
  void shouldAddValue() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
    Assertions.assertThat(loyaltyPoints.add(5).value()).isEqualTo(15);
  }

  @Test
  void shouldCompareValues() {
    LoyaltyPoints lower = new LoyaltyPoints(5);
    LoyaltyPoints higher = new LoyaltyPoints(10);

    Assertions.assertThat(lower.compareTo(higher)).isNegative();
    Assertions.assertThat(higher.compareTo(lower)).isPositive();
  }

  @Test
  void shouldConvertToStringUsingValue() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

    Assertions.assertThat(loyaltyPoints.toString()).isEqualTo("10");
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new LoyaltyPoints(null));
  }

  @Test
  void shouldNotCreateWithNegativeValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new LoyaltyPoints(-1));
  }

  @Test
  void shouldNotAddNullLoyaltyPoints() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> loyaltyPoints.add((LoyaltyPoints) null));
  }

  @Test
  void shouldNotAddValue() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> loyaltyPoints.add(-5));

    Assertions.assertThat(loyaltyPoints.value()).isEqualTo(10);
  }

  @Test
  void shouldNotAddZeroValue() {
    LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> loyaltyPoints.add(0));

    Assertions.assertThat(loyaltyPoints.value()).isEqualTo(10);
  }

}
