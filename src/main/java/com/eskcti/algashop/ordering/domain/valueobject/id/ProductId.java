package com.eskcti.algashop.ordering.domain.valueobject.id;

import java.util.Objects;
import java.util.UUID;
import com.eskcti.algashop.ordering.domain.utility.IdGenerator;

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
