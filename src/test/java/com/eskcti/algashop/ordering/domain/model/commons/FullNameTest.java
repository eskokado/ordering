package com.eskcti.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.FullName;

import org.assertj.core.api.Assertions;

class FullNameTest {

  @Test
  void shouldCreateWithValidValues() {
    FullName fullName = new FullName("  John  ", "  Doe  ");

    Assertions.assertThat(fullName.firstName()).isEqualTo("John");
    Assertions.assertThat(fullName.lastName()).isEqualTo("Doe");
    Assertions.assertThat(fullName.toString()).isEqualTo("John Doe");
  }

  @Test
  void shouldNotCreateWithNullFirstName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new FullName(null, "Doe"));
  }

  @Test
  void shouldNotCreateWithNullLastName() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new FullName("John", null));
  }

  @Test
  void shouldNotCreateWithBlankFirstName() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new FullName(" ", "Doe"));
  }

  @Test
  void shouldNotCreateWithBlankLastName() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new FullName("John", ""));
  }
}
