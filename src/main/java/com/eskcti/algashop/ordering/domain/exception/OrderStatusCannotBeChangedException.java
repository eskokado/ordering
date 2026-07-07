package com.eskcti.algashop.ordering.domain.exception;

import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.entity.OrderStatus;
import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

public class OrderStatusCannotBeChangedException extends DomainException {

  public OrderStatusCannotBeChangedException(OrderId id, OrderStatus status, OrderStatus newStatus) {
    super(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED, id, status, newStatus));
  }
}