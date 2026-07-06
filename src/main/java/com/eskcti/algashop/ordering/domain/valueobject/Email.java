package com.eskcti.algashop.ordering.domain.valueobject;

import com.eskcti.algashop.ordering.domain.validator.FieldValidations;

public record Email(String value) {
  public Email {
    FieldValidations.requiresValidEmail(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
