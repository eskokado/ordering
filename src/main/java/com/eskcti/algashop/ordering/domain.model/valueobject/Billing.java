package com.eskcti.algashop.ordering.domain.model.valueobject;

import java.util.Objects;
import lombok.Builder;

@Builder
public record Billing(FullName fullName, Document document, Phone phone, Email email, Address address) {
  public Billing {
    Objects.requireNonNull(fullName);
    Objects.requireNonNull(document);
    Objects.requireNonNull(phone);
    Objects.requireNonNull(email);
    Objects.requireNonNull(address);
  }
}