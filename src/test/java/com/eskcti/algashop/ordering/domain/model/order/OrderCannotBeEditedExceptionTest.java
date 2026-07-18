package com.eskcti.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.ErrorMessages;
import com.eskcti.algashop.ordering.domain.model.order.OrderCannotBeEditedException;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;

class OrderCannotBeEditedExceptionTest {

  @Test
  void shouldCreateWithOrderIdAndStatus() {
    OrderId id = new OrderId();
    OrderStatus status = OrderStatus.PLACED;

    OrderCannotBeEditedException exception = new OrderCannotBeEditedException(id, status);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED, id, status));
  }
}
