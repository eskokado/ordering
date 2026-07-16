package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.Billing;
import com.eskcti.algashop.ordering.domain.model.valueobject.Shipping;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
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
    return orderPersistenceEntity;
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
        .address(this.toAddressEmbeddable(billing.address()))
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
