package com.eskcti.algashop.ordering.domain.model.order;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.IdGenerator;

import io.hypersistence.tsid.TSID;

public record OrderItemId(TSID value) {

  public OrderItemId {
    Objects.requireNonNull(value);
  }

  public OrderItemId() {
    this(IdGenerator.generateTSID());
  }

  public OrderItemId(Long value) {
    this(TSID.from(value));
  }

  public OrderItemId(String value) {
    this(TSID.from(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
