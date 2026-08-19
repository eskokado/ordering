package com.eskcti.algashop.ordering.core.domain.model.order;

import static com.eskcti.algashop.ordering.core.domain.model.ErrorMessages.*;

import com.eskcti.algashop.ordering.core.domain.model.DomainException;

public class OrderStatusCannotBeChangedException extends DomainException {

  public OrderStatusCannotBeChangedException(OrderId id, OrderStatus status, OrderStatus newStatus) {
    super(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED, id, status, newStatus));
  }
}