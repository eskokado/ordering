package com.eskcti.algashop.ordering.domain.entity;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

  @Test
  void given_validData_whenCreateOrder_shouldExposeFields() {
    Order order = OrderTestDataBuilder.validOrder();

    assertThat(order.id()).isNotNull();
    assertThat(order.customerId()).isNotNull();
    assertThat(order.totalAmount()).isEqualTo(new Money("100.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(2));
    assertThat(order.placedAt()).isNotNull();
    assertThat(order.paidAt()).isNull();
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
    assertThat(order.billing()).isNotNull();
    assertThat(order.shipping()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    assertThat(order.shippingCost()).isEqualTo(new Money("10.00"));
    assertThat(order.expectedDeliveryDate()).isNotNull();
    assertThat(order.items()).hasSize(1);
  }

  @Test
  void given_paidOrder_whenCreate_shouldAllowOptionalTimestamps() {
    OffsetDateTime now = OffsetDateTime.now();
    Order order = OrderTestDataBuilder.order(builder -> builder
        .withPaidAt(now)
        .withReadyAt(now)
        .withStatus(OrderStatus.PAID)
        .withPaymentMethod(PaymentMethod.GATEWAY_BALANCE));

    assertThat(order.paidAt()).isEqualTo(now);
    assertThat(order.readyAt()).isEqualTo(now);
    assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
  }

  @Test
  void given_ordersWithSameId_whenCompare_shouldBeEqual() {
    OrderId orderId = new OrderId(99L);
    Order first = OrderTestDataBuilder.order(builder -> builder.withId(orderId));
    Order second = OrderTestDataBuilder.order(builder -> builder.withId(orderId));

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_ordersWithDifferentId_whenCompare_shouldNotBeEqual() {
    Order first = OrderTestDataBuilder.order(builder -> builder.withId(new OrderId(1L)));
    Order second = OrderTestDataBuilder.order(builder -> builder.withId(new OrderId(2L)));

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-an-order");
  }

  @Test
  void given_nullRequiredFields_whenCreateOrder_shouldGenerateException() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withId(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withCustomerId(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withTotalAmount(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withTotalItems(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withBilling(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withShipping(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withStatus(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withPaymentMethod(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.order(builder -> builder.withItems(null)));
  }
}
