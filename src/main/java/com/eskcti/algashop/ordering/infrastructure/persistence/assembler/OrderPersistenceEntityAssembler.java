package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderItem;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.Billing;
import com.eskcti.algashop.ordering.domain.model.valueobject.Recipient;
import com.eskcti.algashop.ordering.domain.model.valueobject.Shipping;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
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
    orderPersistenceEntity.setVersion(order.version());
    orderPersistenceEntity.setBilling(toBillingEmbeddable(order.billing()));
    orderPersistenceEntity.setShipping(toShippingEmbeddable(order.shipping()));
    Set<OrderItemPersistenceEntity> mergedItems = mergeItems(order, orderPersistenceEntity);
    orderPersistenceEntity.replaceItems(mergedItems);
    return orderPersistenceEntity;
  }

  private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity orderPersistenceEntity) {
    Set<OrderItem> newOrUpdatedItems = order.items();

    if (newOrUpdatedItems.isEmpty()) {
      return new HashSet<>();
    }

    Set<OrderItemPersistenceEntity> existingItems = orderPersistenceEntity.getItems();
    if (existingItems == null || existingItems.isEmpty()) {
      return newOrUpdatedItems.stream()
          .map(orderItem -> fromDomain(orderItem))
          .collect(Collectors.toSet());
    }

    Map<Long, OrderItemPersistenceEntity> existingItemMap = existingItems.stream()
        .collect(Collectors.toMap(OrderItemPersistenceEntity::getId, item -> item));

    return newOrUpdatedItems.stream()
        .map(orderItem -> {
          OrderItemPersistenceEntity itemPersistence = existingItemMap.getOrDefault(
              orderItem.id().value().toLong(), new OrderItemPersistenceEntity());
          return merge(itemPersistence, orderItem);
        })
        .collect(Collectors.toSet());
  }

  public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
    return merge(new OrderItemPersistenceEntity(), orderItem);
  }

  private OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity,
      OrderItem orderItem) {
    orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
    orderItemPersistenceEntity.setProductId(orderItem.productId().value());
    orderItemPersistenceEntity.setProductName(orderItem.productName().value());
    orderItemPersistenceEntity.setPrice(orderItem.price().value());
    orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
    orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
    return orderItemPersistenceEntity;
  }

  private BillingEmbeddable toBillingEmbeddable(Billing billing) {
    if (billing == null) {
      return null;
    }
    return BillingEmbeddable.builder()
        .firstName(billing.fullName().firstName())
        .lastName(billing.fullName().lastName())
        .document(billing.document().value())
        .phone(billing.phone().value())
        .email(billing.email().value())
        .address(toAddressEmbeddable(billing.address()))
        .build();
  }

  private AddressEmbeddable toAddressEmbeddable(Address address) {
    return AddressEmbeddable.builder()
        .city(address.city())
        .state(address.state())
        .number(address.number())
        .street(address.street())
        .complement(address.complement())
        .neighborhood(address.neighborhood())
        .zipCode(address.zipCode().value())
        .build();
  }

  private ShippingEmbeddable toShippingEmbeddable(Shipping shipping) {
    if (shipping == null) {
      return null;
    }
    var builder = ShippingEmbeddable.builder()
        .expectedDate(shipping.expectedDate())
        .cost(shipping.cost().value())
        .address(toAddressEmbeddable(shipping.address()))
        .recipient(
            RecipientEmbeddable.builder()
                .firstName(shipping.recipient().fullName().firstName())
                .lastName(shipping.recipient().fullName().lastName())
                .document(shipping.recipient().document().value())
                .phone(shipping.recipient().phone().value())
                .build());
    return builder.build();
  }

}
