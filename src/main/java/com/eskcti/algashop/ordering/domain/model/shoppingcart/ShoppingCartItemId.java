package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.util.UUID;

import com.eskcti.algashop.ordering.domain.model.IdGenerator;

import java.util.Objects;

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