package com.eskcti.algashop.ordering.domain.model.commons;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;

import org.assertj.core.api.Assertions;

class MoneyTest {

  @Test
  void shouldCreateWithValidValue() {
    Money money = new Money("10.556");

    Assertions.assertThat(money.value()).isEqualByComparingTo(new BigDecimal("10.56"));
    Assertions.assertThat(money.toString()).isEqualTo("10.56");
  }

  @Test
  void shouldExposeZeroConstant() {
    Assertions.assertThat(Money.ZERO.value()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void shouldAddValues() {
    Money result = new Money("10.00").add(new Money("5.50"));

    Assertions.assertThat(result.value()).isEqualByComparingTo(new BigDecimal("15.50"));
  }

  @Test
  void shouldMultiplyByQuantity() {
    Money result = new Money("10.00").multiply(new Quantity(3));

    Assertions.assertThat(result.value()).isEqualByComparingTo(new BigDecimal("30.00"));
  }

  @Test
  void shouldDivideValues() {
    Money result = new Money("10.00").divide(new Money("4.00"));

    Assertions.assertThat(result.value()).isEqualByComparingTo(new BigDecimal("2.50"));
  }

  @Test
  void shouldCompareValues() {
    Money lower = new Money("10.00");
    Money higher = new Money("20.00");

    Assertions.assertThat(lower.compareTo(higher)).isNegative();
    Assertions.assertThat(higher.compareTo(lower)).isPositive();
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Money((BigDecimal) null));
  }

  @Test
  void shouldNotCreateWithNegativeValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Money("-1.00"));
  }

  @Test
  void shouldNotMultiplyWithNullQuantity() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Money("10.00").multiply(null));
  }

  @Test
  void shouldNotMultiplyWithInvalidQuantity() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Money("10.00").multiply(Quantity.ZERO));
  }

  @Test
  void shouldNotAddNullMoney() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Money("10.00").add(null));
  }
}
