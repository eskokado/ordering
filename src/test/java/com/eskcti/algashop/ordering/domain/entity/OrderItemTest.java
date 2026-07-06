package com.eskcti.algashop.ordering.domain.entity;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderItemId;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

  @Test
  void given_validData_whenCreateOrderItem_shouldExposeFields() {
    OrderItem orderItem = OrderTestDataBuilder.validOrderItem();

    assertThat(orderItem.id()).isNotNull();
    assertThat(orderItem.orderId()).isNotNull();
    assertThat(orderItem.productId()).isNotNull();
    assertThat(orderItem.productName()).isEqualTo(new ProductName("Notebook"));
    assertThat(orderItem.price()).isEqualTo(new Money("50.00"));
    assertThat(orderItem.quantity()).isEqualTo(new Quantity(2));
    assertThat(orderItem.totalAmount()).isEqualTo(new Money("100.00"));
  }

  @Test
  void given_orderItemsWithSameId_whenCompare_shouldBeEqual() {
    OrderItemId orderItemId = new OrderItemId(42L);
    OrderItem first = OrderTestDataBuilder.orderItem(builder -> builder.withId(orderItemId));
    OrderItem second = OrderTestDataBuilder.orderItem(builder -> builder.withId(orderItemId));

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_orderItemsWithDifferentId_whenCompare_shouldNotBeEqual() {
    OrderItem first = OrderTestDataBuilder.orderItem(builder -> builder.withId(new OrderItemId(1L)));
    OrderItem second = OrderTestDataBuilder.orderItem(builder -> builder.withId(new OrderItemId(2L)));

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-an-order-item");
  }

  @Test
  void given_nullRequiredFields_whenCreateOrderItem_shouldGenerateException() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withId(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withOrderId(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withProductId(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withProductName(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withPrice(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withQuantity(null)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.orderItem(builder -> builder.withTotalAmount(null)));
  }
}
