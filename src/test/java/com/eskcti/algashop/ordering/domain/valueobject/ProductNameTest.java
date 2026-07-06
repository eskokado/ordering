package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class ProductNameTest {

  @Test
  void shouldCreateWithValidValue() {
    ProductName productName = new ProductName("Notebook");

    Assertions.assertThat(productName.value()).isEqualTo("Notebook");
    Assertions.assertThat(productName.toString()).isEqualTo("Notebook");
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new ProductName(null));
  }

  @Test
  void shouldNotCreateWithBlankValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new ProductName(" "));
  }
}
