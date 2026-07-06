package com.eskcti.algashop.ordering.domain.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

import com.eskcti.algashop.ordering.domain.valueobject.BillingInfo;
import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.ShippingInfo;
import com.eskcti.algashop.ordering.domain.valueobject.ValueObjectTestFixtures;
import com.eskcti.algashop.ordering.domain.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderItemId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

public final class OrderTestDataBuilder {

  private OrderTestDataBuilder() {
  }

  public static Order draftOrder() {
    return Order.draft(new CustomerId());
  }

  public static Order.ExistingOrderBuilder existingOrder() {
    OrderId orderId = new OrderId(1L);
    return Order.existing()
        .id(orderId)
        .customerId(new CustomerId())
        .totalAmount(new Money("100.00"))
        .totalItems(new Quantity(2))
        .placedAt(OffsetDateTime.now())
        .paidAt(null)
        .canceledAt(null)
        .readyAt(null)
        .billing(BillingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build())
        .shipping(ShippingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build())
        .status(OrderStatus.PLACED)
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .shippingCost(new Money("10.00"))
        .expectedDeliveryDate(LocalDate.now().plusDays(5))
        .items(Set.of(existingOrderItem(orderId).build()));
  }

  public static OrderItem.ExistingOrderItemBuilder existingOrderItem(OrderId orderId) {
    return OrderItem.existing()
        .id(new OrderItemId(1L))
        .orderId(orderId)
        .productId(new ProductId())
        .productName(new ProductName("Notebook"))
        .price(new Money("50.00"))
        .quantity(new Quantity(2))
        .totalAmount(new Money("100.00"));
  }

  public static OrderItem.BrandNewOrderItemBuilder brandNewOrderItem(OrderId orderId) {
    return OrderItem.brandNew()
        .orderId(orderId)
        .productId(new ProductId())
        .productName(new ProductName("Notebook"))
        .price(new Money("50.00"))
        .quantity(new Quantity(2));
  }
}
