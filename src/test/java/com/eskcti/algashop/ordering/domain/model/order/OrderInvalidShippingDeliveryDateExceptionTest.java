package com.eskcti.algashop.ordering.domain.model.order;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderInvalidShippingDeliveryDateException;

class OrderInvalidShippingDeliveryDateExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    OrderId orderId = new OrderId(1L);
    OrderInvalidShippingDeliveryDateException exception = new OrderInvalidShippingDeliveryDateException(orderId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST, orderId));
  }
}
