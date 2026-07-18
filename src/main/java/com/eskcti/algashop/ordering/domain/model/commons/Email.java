package com.eskcti.algashop.ordering.domain.model.commons;

import com.eskcti.algashop.ordering.domain.model.FieldValidations;

public record Email(String value) {
  public Email {
    FieldValidations.requiresValidEmail(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
