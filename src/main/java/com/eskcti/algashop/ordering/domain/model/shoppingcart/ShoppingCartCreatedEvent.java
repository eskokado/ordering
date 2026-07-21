package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;

public record ShoppingCartCreatedEvent(
    ShoppingCartId shoppingCartId,
    CustomerId customerId,
    OffsetDateTime createdAt) {
}