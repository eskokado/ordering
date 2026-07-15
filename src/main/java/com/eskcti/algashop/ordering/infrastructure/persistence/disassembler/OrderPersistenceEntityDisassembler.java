package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
import java.util.HashSet;

@Component
public class OrderPersistenceEntityDisassembler {

  public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
    return Order.existing()
        .id(new OrderId(persistenceEntity.getId()))
        .customerId(new CustomerId(persistenceEntity.getCustomerId()))
        .totalAmount(
            persistenceEntity.getTotalAmount() != null ? new Money(persistenceEntity.getTotalAmount()) : Money.ZERO)
        .totalItems(
            persistenceEntity.getTotalItems() != null ? new Quantity(persistenceEntity.getTotalItems()) : Quantity.ZERO)
        .status(persistenceEntity.getStatus() != null ? OrderStatus.valueOf(persistenceEntity.getStatus())
            : OrderStatus.DRAFT)
        .paymentMethod(
            persistenceEntity.getPaymentMethod() != null ? PaymentMethod.valueOf(persistenceEntity.getPaymentMethod())
                : null)
        .placedAt(persistenceEntity.getPlacedAt())
        .paidAt(persistenceEntity.getPaidAt())
        .canceledAt(persistenceEntity.getCanceledAt())
        .readyAt(persistenceEntity.getReadyAt())
        .items(new HashSet<>())
        .build();
  }

}