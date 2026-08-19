package com.eskcti.algashop.ordering.core.domain.model.order;

import java.util.Objects;
import java.util.UUID;

import com.eskcti.algashop.ordering.core.domain.model.IdGenerator;

public record CreditCardId(UUID id) {
    public CreditCardId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public CreditCardId {
        Objects.requireNonNull(id);
    }
}
