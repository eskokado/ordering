package com.eskcti.algashop.ordering.core.domain.model.order;

import java.util.Objects;

import com.eskcti.algashop.ordering.core.domain.model.commons.Document;
import com.eskcti.algashop.ordering.core.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.core.domain.model.commons.Phone;

import lombok.Builder;

@Builder
public record Recipient(FullName fullName, Document document, Phone phone) {
  public Recipient {
    Objects.requireNonNull(fullName);
    Objects.requireNonNull(document);
    Objects.requireNonNull(phone);
  }
}