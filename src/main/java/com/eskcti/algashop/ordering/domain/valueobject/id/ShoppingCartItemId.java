package com.eskcti.algashop.ordering.domain.valueobject.id;

import java.util.UUID;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.utility.IdGenerator;

public record ShoppingCartItemId(UUID value) {

  public ShoppingCartItemId {
    Objects.requireNonNull(value);
  }

  public ShoppingCartItemId() {
    this(IdGenerator.generateTimeBasedUUID());
  }

  public ShoppingCartItemId(String value) {
    this(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}