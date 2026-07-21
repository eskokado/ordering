package com.eskcti.algashop.ordering.domain.model.order;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;

public record OrderCanceledEvent(OrderId orderId, CustomerId customerId, OffsetDateTime canceledAt) {

}
