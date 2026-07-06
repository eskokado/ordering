package com.eskcti.algashop.ordering.domain.valueobject.id;

import java.util.Objects;
import io.hypersistence.tsid.TSID;
import com.eskcti.algashop.ordering.domain.utility.IdGenerator;

public record OrderItemId(TSID value) {

  public OrderItemId {
    Objects.requireNonNull(value);
  }

  public OrderItemId() {
    this(IdGenerator.gererateTSID());
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
