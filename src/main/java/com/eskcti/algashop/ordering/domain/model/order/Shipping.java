package com.eskcti.algashop.ordering.domain.model.order;

import java.time.LocalDate;
import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Money;

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