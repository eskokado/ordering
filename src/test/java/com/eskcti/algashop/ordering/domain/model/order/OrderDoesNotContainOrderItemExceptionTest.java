package com.eskcti.algashop.ordering.domain.model.order;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.order.OrderDoesNotContainOrderItemException;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderItemId;

class OrderDoesNotContainOrderItemExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    OrderId orderId = new OrderId(1L);
    OrderItemId orderItemId = new OrderItemId(2L);
    OrderDoesNotContainOrderItemException exception = new OrderDoesNotContainOrderItemException(orderId, orderItemId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, orderId, orderItemId));
  }
}
