package com.eskcti.algashop.ordering.domain.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.*;

class OrderCannotBePlacedExceptionTest {

  private final OrderId orderId = new OrderId(1L);

  @Test
  void shouldCreateWithNoItemsMessage() {
    OrderCannotBePlacedException exception = OrderCannotBePlacedException.noItems(orderId);
    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS, orderId));
  }

  @Test
  void shouldCreateWithNoShippingInfoMessage() {
    OrderCannotBePlacedException exception = OrderCannotBePlacedException.noShippingInfo(orderId);
    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, orderId));
  }

  @Test
  void shouldCreateWithNoBillingInfoMessage() {
    OrderCannotBePlacedException exception = OrderCannotBePlacedException.noBillingInfo(orderId);
    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, orderId));
  }

  @Test
  void shouldCreateWithNoPaymentMethodMessage() {
    OrderCannotBePlacedException exception = OrderCannotBePlacedException.noPaymentMethod(orderId);
    Assertions.assertThat(exception.getMessage())
        .isEqualTo(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, orderId));
  }
}
