package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class EmailTest {

  @Test
  void shouldCreateWithValidEmail() {
    Email email = new Email("john.doe@gmail.com");

    Assertions.assertThat(email.value()).isEqualTo("john.doe@gmail.com");
    Assertions.assertThat(email.toString()).isEqualTo("john.doe@gmail.com");
  }

  @Test
  void shouldNotCreateWithNullEmail() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Email(null));
  }

  @Test
  void shouldNotCreateWithBlankEmail() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Email(" "));
  }

  @Test
  void shouldNotCreateWithInvalidEmail() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Email("invalid-email"));
  }
}
