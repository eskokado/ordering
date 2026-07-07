package com.eskcti.algashop.ordering.domain.exception;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

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
