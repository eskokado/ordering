package com.eskcti.algashop.ordering.domain.valueobject.id;

import java.util.Objects;
import io.hypersistence.tsid.TSID;
import com.eskcti.algashop.ordering.domain.utility.IdGenerator;

public record OrderId(TSID value) {

  public OrderId {
    Objects.requireNonNull(value);
  }

  public OrderId() {
    this(IdGenerator.gererateTSID());
  }

  public OrderId(Long value) {
    this(TSID.from(value));
  }

  public OrderId(String value) {
    this(TSID.from(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
