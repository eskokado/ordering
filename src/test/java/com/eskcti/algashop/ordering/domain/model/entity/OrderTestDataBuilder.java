package com.eskcti.algashop.ordering.domain.model.entity;

import java.time.LocalDate;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.valueobject.*;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;

public class OrderTestDataBuilder {

  private CustomerId customerId = new CustomerId();

  private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

  private Shipping shipping = aShipping();
  private Billing billing = aBilling();

  private boolean withItems = true;

  private OrderStatus status = OrderStatus.DRAFT;

  private OrderTestDataBuilder() {

  }

  public static OrderTestDataBuilder anOrder() {
    return new OrderTestDataBuilder();
  }

  public static Order draftOrder() {
    return Order.draft(new CustomerId());
  }

  public static OrderTestDataBuilder aPlacedOrder() {
    return anOrder()
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .status(OrderStatus.PLACED);
  }

  public Order build() {
    Order order = Order.draft(customerId);
    order.changeShipping(shipping);
    order.changeBilling(billing);
    order.changePaymentMethod(paymentMethod);

    if (withItems) {
      order.addItem(ProductTestDataBuilder.aProduct().build(),
          new Quantity(2));

      order.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(),
          new Quantity(1));
    }

    switch (this.status) {
      case DRAFT -> {
      }
      case PLACED -> {
        order.place();
      }
      case PAID -> {
        order.place();
        order.markAsPaid();
      }
      case CANCELED -> setStatus(order, OrderStatus.CANCELED);
      case READY -> setStatus(order, OrderStatus.READY);
    }

    return order;
  }

  public static Billing aBilling() {
    return Billing.builder()
        .address(anAddress())
        .document(new Document("225-09-1992"))
        .phone(new Phone("123-111-9911"))
        .email(new Email("john.doe@gmail.com"))
        .fullName(new FullName("John", "Doe")).build();
  }

  public static Shipping aShipping() {
    return Shipping.builder()
        .cost(new Money("10"))
        .expectedDate(LocalDate.now().plusWeeks(1))
        .address(anAddress())
        .recipient(Recipient.builder()
            .fullName(new FullName("John", "Doe"))
            .document(new Document("112-33-2321"))
            .phone(new Phone("111-441-1244"))
            .build())
        .build();
  }

  public static Address anAddress() {
    return Address.builder()
        .street("Bourbon Street")
        .number("1234")
        .neighborhood("North Ville")
        .complement("apt. 11")
        .city("Montfort")
        .state("South Carolina")
        .zipCode(new ZipCode("79911")).build();
  }

  public static Shipping aShippingAlt() {
    return Shipping.builder()
        .cost(new Money("20.00"))
        .expectedDate(LocalDate.now().plusWeeks(2))
        .address(anAddressAlt())
        .recipient(Recipient.builder()
            .fullName(new FullName("Mary", "Jones"))
            .document(new Document("552-11-4333"))
            .phone(new Phone("54-454-1144"))
            .build())
        .build();
  }

  public static Address anAddressAlt() {
    return Address.builder()
        .street("Sansome Street")
        .number("875")
        .neighborhood("Sansome")
        .city("San Francisco")
        .state("California")
        .zipCode(new ZipCode("08040"))
        .build();
  }

  public OrderTestDataBuilder customerId(CustomerId customerId) {
    this.customerId = customerId;
    return this;
  }

  public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  public OrderTestDataBuilder shippingInfo(Shipping shipping) {
    this.shipping = shipping;
    return this;
  }

  public OrderTestDataBuilder billingInfo(Billing billing) {
    this.billing = billing;
    return this;
  }

  public OrderTestDataBuilder withItems(boolean withItems) {
    this.withItems = withItems;
    return this;
  }

  public OrderTestDataBuilder status(OrderStatus status) {
    this.status = status;
    return this;
  }

  private static void setStatus(Order order, OrderStatus status) {
    try {
      var field = Order.class.getDeclaredField("status");
      field.setAccessible(true);
      field.set(order, status);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

}