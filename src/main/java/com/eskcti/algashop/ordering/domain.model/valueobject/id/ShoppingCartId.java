package com.eskcti.algashop.ordering.domain.model.valueobject.id;

import java.util.UUID;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;

import java.util.Objects;

public record ShoppingCartId(UUID value) {

  public ShoppingCartId {
    Objects.requireNonNull(value);
  }

  public ShoppingCartId() {
    this(IdGenerator.generateTimeBasedUUID());
  }

  public ShoppingCartId(String value) {
    this(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
