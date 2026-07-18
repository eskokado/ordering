package com.eskcti.algashop.ordering.domain.model.product;

import com.eskcti.algashop.ordering.domain.model.FieldValidations;

public record ProductName(String value) {

  public ProductName {
    FieldValidations.requiresNonBlank(value);
  }

  @Override
  public String toString() {
    return value;
  }

}
