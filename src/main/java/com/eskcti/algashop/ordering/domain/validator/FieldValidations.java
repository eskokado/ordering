package com.eskcti.algashop.ordering.domain.validator;

import java.util.Objects;
import org.apache.commons.validator.routines.EmailValidator;

public class FieldValidations {
  private FieldValidations() {

  }

  public static void requiresNonBlank(String value) {
    requiresNonBlank(value, "");
  }

  public static void requiresNonBlank(String value, String errorMessage) {
    Objects.requireNonNull(value);
    if (value.isBlank()) {
      throw new IllegalArgumentException();
    }
  }

  public static void requiresValidEmail(String email) {
    requiresValidEmail(email, null);
  }

  public static void requiresValidEmail(String email, String errorMessage) {
    Objects.requireNonNull(email, errorMessage);
    if (email.isBlank()) {
      throw new IllegalArgumentException(errorMessage);
    }
    if (!EmailValidator.getInstance().isValid(email)) {
      throw new IllegalArgumentException(errorMessage);
    }
  }
}