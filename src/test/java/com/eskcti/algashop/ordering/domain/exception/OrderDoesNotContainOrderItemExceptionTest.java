package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderItemId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

class OrderDoesNotContainOrderItemExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    OrderId orderId = new OrderId(1L);
    OrderItemId orderItemId = new OrderItemId(2L);
    OrderDoesNotContainOrderItemException exception =
        new OrderDoesNotContainOrderItemException(orderId, orderItemId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, orderId, orderItemId));
  }
}
