package com.eskcti.algashop.ordering.domain.model.exception;

import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO;
import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS;
import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD;
import static com.eskcti.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO;

import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;

public class OrderCannotBePlacedException extends DomainException {

  private OrderCannotBePlacedException(String message) {
    super(message);
  }

  public static OrderCannotBePlacedException noItems(OrderId id) {
    return new OrderCannotBePlacedException(
        String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS, id));
  }

  public static OrderCannotBePlacedException noShippingInfo(OrderId id) {
    return new OrderCannotBePlacedException(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, id));
  }

  public static OrderCannotBePlacedException noBillingInfo(OrderId id) {
    return new OrderCannotBePlacedException(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, id));
  }

  public static OrderCannotBePlacedException noPaymentMethod(OrderId id) {
    return new OrderCannotBePlacedException(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, id));
  }
}