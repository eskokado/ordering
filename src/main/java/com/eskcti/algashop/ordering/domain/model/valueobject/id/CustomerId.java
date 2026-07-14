package com.eskcti.algashop.ordering.domain.model.valueobject.id;

import java.util.Objects;
import java.util.UUID;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;

public record CustomerId(UUID value) {

  public CustomerId() {
    this(IdGenerator.generateTimeBasedUUID());
  }

  public CustomerId(UUID value) {
    Objects.requireNonNull(value);
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
