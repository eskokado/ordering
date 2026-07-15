package com.eskcti.algashop.ordering.infrastructure.persistence.provider;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.repository.Orders;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {

  private final OrderPersistenceEntityRepository persistenceRepository;

  @Override
  public Optional<Order> ofId(OrderId orderId) {
    return persistenceRepository.findById(orderId.value().toLong())
        .map(this::toDomain);
  }

  @Override
  public boolean exists(OrderId orderId) {
    return persistenceRepository.existsById(orderId.value().toLong());
  }

  @Override
  public void add(Order aggregateRoot) {
    var persistenceEntity = toPersistence(aggregateRoot);
    persistenceRepository.saveAndFlush(persistenceEntity);
  }

  @Override
  public int count() {
    return (int) persistenceRepository.count();
  }

  private OrderPersistenceEntity toPersistence(Order order) {
    return OrderPersistenceEntity.builder()
        .id(order.id().value().toLong())
        .customerId(order.customerId().value())
        .totalAmount(order.totalAmount().value())
        .totalItems(order.totalItems().value())
        .status(order.status() != null ? order.status().name() : null)
        .paymentMethod(order.paymentMethod() != null ? order.paymentMethod().name() : null)
        .placedAt(order.placedAt())
        .paidAt(order.paidAt())
        .canceledAt(order.canceledAt())
        .readyAt(order.readyAt())
        .build();
  }

  private Order toDomain(OrderPersistenceEntity entity) {
    return Order.existing()
        .id(new OrderId(entity.getId()))
        .customerId(new CustomerId(entity.getCustomerId()))
        .totalAmount(entity.getTotalAmount() != null ? new Money(entity.getTotalAmount()) : Money.ZERO)
        .totalItems(entity.getTotalItems() != null ? new Quantity(entity.getTotalItems()) : Quantity.ZERO)
        .status(entity.getStatus() != null ? OrderStatus.valueOf(entity.getStatus()) : null)
        .paymentMethod(entity.getPaymentMethod() != null ? PaymentMethod.valueOf(entity.getPaymentMethod()) : null)
        .placedAt(entity.getPlacedAt())
        .paidAt(entity.getPaidAt())
        .canceledAt(entity.getCanceledAt())
        .readyAt(entity.getReadyAt())
        .items(new java.util.HashSet<>())
        .build();
  }
}
