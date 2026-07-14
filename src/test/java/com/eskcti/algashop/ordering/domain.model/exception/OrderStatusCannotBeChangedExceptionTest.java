package com.eskcti.algashop.ordering.domain.model.exception;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

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
