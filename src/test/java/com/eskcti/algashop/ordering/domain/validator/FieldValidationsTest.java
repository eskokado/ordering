package com.eskcti.algashop.ordering.domain.validator;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class FieldValidationsTest {

  @Test
  void shouldAcceptNonBlankValue() {
    FieldValidations.requiresNonBlank("valid");
    FieldValidations.requiresNonBlank("valid", "error");
  }

  @Test
  void shouldRejectNullOrBlankValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> FieldValidations.requiresNonBlank(null));

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> FieldValidations.requiresNonBlank(" "));
  }

  @Test
  void shouldAcceptValidEmail() {
    FieldValidations.requiresValidEmail("john.doe@gmail.com");
    FieldValidations.requiresValidEmail("john.doe@gmail.com", "invalid email");
  }

  @Test
  void shouldRejectInvalidEmail() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> FieldValidations.requiresValidEmail(null));

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> FieldValidations.requiresValidEmail(" "));

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> FieldValidations.requiresValidEmail("invalid-email", "invalid email"))
        .withMessage("invalid email");
  }
}
