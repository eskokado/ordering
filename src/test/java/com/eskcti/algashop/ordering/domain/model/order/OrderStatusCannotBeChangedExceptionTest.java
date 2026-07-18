package com.eskcti.algashop.ordering.domain.model.order;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

import org.assertj.core.api.Assertions;

class OrderStatusCannotBeChangedExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    OrderId orderId = new OrderId(1L);
    OrderStatusCannotBeChangedException exception = new OrderStatusCannotBeChangedException(
        orderId, OrderStatus.PLACED, OrderStatus.DRAFT);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED, orderId, OrderStatus.PLACED, OrderStatus.DRAFT));
  }
}
