package com.eskcti.algashop.ordering.domain.valueobject;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.exception.ErrorMessages;

class BirthDateTest {

  @Test
  void shouldCreateWithPastDate() {
    LocalDate date = LocalDate.of(1991, 7, 5);
    BirthDate birthDate = new BirthDate(date);

    Assertions.assertThat(birthDate.value()).isEqualTo(date);
    Assertions.assertThat(birthDate.toString()).isEqualTo("1991-07-05");
    Assertions.assertThat(birthDate.age()).isPositive();
  }

  @Test
  void shouldNotCreateWithNullDate() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new BirthDate(null));
  }

  @Test
  void shouldNotCreateWithFutureDate() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new BirthDate(LocalDate.now().plusDays(1)))
        .withMessage(ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
  }
}
