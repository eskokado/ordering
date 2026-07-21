package com.eskcti.algashop.ordering.domain.model.customer;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;

public record CustomerRegisteredEvent(CustomerId customerId,
    OffsetDateTime registeredAt,
    FullName fullName,
    Email email) {
}