package com.eskcti.algashop.ordering.core.domain.model.order;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;

public record OrderPaidEvent(OrderId orderId, CustomerId customerId, OffsetDateTime paidAt) {

}
