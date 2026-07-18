package com.eskcti.algashop.ordering.domain.model.order;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;

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