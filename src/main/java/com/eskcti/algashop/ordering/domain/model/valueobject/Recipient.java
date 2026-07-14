package com.eskcti.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

import lombok.Builder;

@Builder
public record Recipient(FullName fullName, Document document, Phone phone) {
  public Recipient {
    Objects.requireNonNull(fullName);
    Objects.requireNonNull(document);
    Objects.requireNonNull(phone);
  }
}