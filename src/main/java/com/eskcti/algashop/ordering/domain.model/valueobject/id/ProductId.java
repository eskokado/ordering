package com.eskcti.algashop.ordering.domain.model.valueobject.id;

import java.util.Objects;
import java.util.UUID;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;

public record ProductId(UUID value) {

  public ProductId {
    Objects.requireNonNull(value);
  }

  public ProductId() {
    this(IdGenerator.generateTimeBasedUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
