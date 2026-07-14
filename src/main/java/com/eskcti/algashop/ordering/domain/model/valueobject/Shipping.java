package com.eskcti.algashop.ordering.domain.model.valueobject;

import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;

@Builder(toBuilder = true)
public record Shipping(Money cost, LocalDate expectedDate, Recipient recipient, Address address) {
  public Shipping {
    Objects.requireNonNull(address);
    Objects.requireNonNull(recipient);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(expectedDate);
  }
}