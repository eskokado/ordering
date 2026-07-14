package com.eskcti.algashop.ordering.domain.model.exception;

import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;

public class OrderCannotBeEditedException extends DomainException {

  public OrderCannotBeEditedException(OrderId id, OrderStatus status) {
    super(String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED, id, status));
  }
}