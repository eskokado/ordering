package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;

@Component
public class OrderPersistenceEntityAssembler {

  public OrderPersistenceEntity fromDomain(Order order) {
    return merge(new OrderPersistenceEntity(), order);
  }

  public OrderPersistenceEntity merge(OrderPersistenceEntity orderPersistenceEntity, Order order) {
    orderPersistenceEntity.setId(order.id().value().toLong());
    orderPersistenceEntity.setCustomerId(order.customerId().value());
    orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
    orderPersistenceEntity.setTotalItems(order.totalItems().value());
    orderPersistenceEntity.setStatus(order.status().name());
    orderPersistenceEntity.setPaymentMethod(order.paymentMethod() != null ? order.paymentMethod().name() : null);
    orderPersistenceEntity.setPlacedAt(order.placedAt());
    orderPersistenceEntity.setPaidAt(order.paidAt());
    orderPersistenceEntity.setCanceledAt(order.canceledAt());
    orderPersistenceEntity.setReadyAt(order.readyAt());
    return orderPersistenceEntity;
  }

}
