package com.eskcti.algashop.ordering.domain.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.function.UnaryOperator;

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

  public static Order validOrder() {
    return order(builder -> builder);
  }

  public static Order order(UnaryOperator<OrderParamsBuilder> customizer) {
    OrderParamsBuilder builder = defaultOrderParams();
    customizer.apply(builder);
    return builder.build();
  }

  public static OrderItem validOrderItem() {
    return orderItem(builder -> builder);
  }

  public static OrderItem orderItem(UnaryOperator<OrderItemParamsBuilder> customizer) {
    OrderItemParamsBuilder builder = defaultOrderItemParams();
    customizer.apply(builder);
    return builder.build();
  }

  static OrderParamsBuilder defaultOrderParams() {
    OrderId orderId = new OrderId(1L);
    OrderItem item = orderItem(builder -> builder.withOrderId(orderId));
    return new OrderParamsBuilder()
        .withId(orderId)
        .withCustomerId(new CustomerId())
        .withTotalAmount(new Money("100.00"))
        .withTotalItems(new Quantity(2))
        .withPlacedAt(OffsetDateTime.now())
        .withBilling(BillingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build())
        .withShipping(ShippingInfo.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .address(ValueObjectTestFixtures.validAddress())
            .build())
        .withStatus(OrderStatus.PLACED)
        .withPaymentMethod(PaymentMethod.CREDIT_CARD)
        .withShippingCost(new Money("10.00"))
        .withExpectedDeliveryDate(LocalDate.now().plusDays(5))
        .withItems(Set.of(item));
  }

  static OrderItemParamsBuilder defaultOrderItemParams() {
    return new OrderItemParamsBuilder()
        .withId(new OrderItemId(1L))
        .withOrderId(new OrderId(1L))
        .withProductId(new ProductId())
        .withProductName(new ProductName("Notebook"))
        .withPrice(new Money("50.00"))
        .withQuantity(new Quantity(2))
        .withTotalAmount(new Money("100.00"));
  }

  static final class OrderParamsBuilder {
    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private BillingInfo billing;
    private ShippingInfo shipping;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private Money shippingCost;
    private LocalDate expectedDeliveryDate;
    private Set<OrderItem> items;

    OrderParamsBuilder withId(OrderId id) {
      this.id = id;
      return this;
    }

    OrderParamsBuilder withCustomerId(CustomerId customerId) {
      this.customerId = customerId;
      return this;
    }

    OrderParamsBuilder withTotalAmount(Money totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    OrderParamsBuilder withTotalItems(Quantity totalItems) {
      this.totalItems = totalItems;
      return this;
    }

    OrderParamsBuilder withPlacedAt(OffsetDateTime placedAt) {
      this.placedAt = placedAt;
      return this;
    }

    OrderParamsBuilder withPaidAt(OffsetDateTime paidAt) {
      this.paidAt = paidAt;
      return this;
    }

    OrderParamsBuilder withCanceledAt(OffsetDateTime canceledAt) {
      this.canceledAt = canceledAt;
      return this;
    }

    OrderParamsBuilder withReadyAt(OffsetDateTime readyAt) {
      this.readyAt = readyAt;
      return this;
    }

    OrderParamsBuilder withBilling(BillingInfo billing) {
      this.billing = billing;
      return this;
    }

    OrderParamsBuilder withShipping(ShippingInfo shipping) {
      this.shipping = shipping;
      return this;
    }

    OrderParamsBuilder withStatus(OrderStatus status) {
      this.status = status;
      return this;
    }

    OrderParamsBuilder withPaymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
      return this;
    }

    OrderParamsBuilder withShippingCost(Money shippingCost) {
      this.shippingCost = shippingCost;
      return this;
    }

    OrderParamsBuilder withExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
      this.expectedDeliveryDate = expectedDeliveryDate;
      return this;
    }

    OrderParamsBuilder withItems(Set<OrderItem> items) {
      this.items = items;
      return this;
    }

    Order build() {
      return new Order(
          id, customerId, totalAmount, totalItems,
          placedAt, paidAt, canceledAt, readyAt,
          billing, shipping, status, paymentMethod,
          shippingCost, expectedDeliveryDate, items);
    }
  }

  static final class OrderItemParamsBuilder {
    private OrderItemId id;
    private OrderId orderId;
    private ProductId productId;
    private ProductName productName;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;

    OrderItemParamsBuilder withId(OrderItemId id) {
      this.id = id;
      return this;
    }

    OrderItemParamsBuilder withOrderId(OrderId orderId) {
      this.orderId = orderId;
      return this;
    }

    OrderItemParamsBuilder withProductId(ProductId productId) {
      this.productId = productId;
      return this;
    }

    OrderItemParamsBuilder withProductName(ProductName productName) {
      this.productName = productName;
      return this;
    }

    OrderItemParamsBuilder withPrice(Money price) {
      this.price = price;
      return this;
    }

    OrderItemParamsBuilder withQuantity(Quantity quantity) {
      this.quantity = quantity;
      return this;
    }

    OrderItemParamsBuilder withTotalAmount(Money totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    OrderItem build() {
      return new OrderItem(id, orderId, productId, productName, price, quantity, totalAmount);
    }
  }
}
