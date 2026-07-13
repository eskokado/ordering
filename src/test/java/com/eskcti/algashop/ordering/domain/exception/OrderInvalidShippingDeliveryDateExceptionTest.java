package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST;

class OrderInvalidShippingDeliveryDateExceptionTest {

  @Test
  void shouldCreateWithFormattedMessage() {
    OrderId orderId = new OrderId(1L);
    OrderInvalidShippingDeliveryDateException exception =
        new OrderInvalidShippingDeliveryDateException(orderId);

    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST, orderId));
  }
}
