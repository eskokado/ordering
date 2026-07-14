package com.eskcti.algashop.ordering.domain.valueobject.id;

import java.util.UUID;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.utility.IdGenerator;

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
