package com.eskcti.algashop.ordering.domain.entity;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderItemId;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

  @Test
  void given_brandNewOrderItem_whenBuild_shouldInitializeDefaults() {
    OrderId orderId = new OrderId(1L);
    OrderItem orderItem = OrderTestDataBuilder.brandNewOrderItem(orderId).build();

    assertThat(orderItem.id()).isNotNull();
    assertThat(orderItem.orderId()).isEqualTo(orderId);
    assertThat(orderItem.productId()).isNotNull();
    assertThat(orderItem.productName()).isEqualTo(new ProductName("Notebook"));
    assertThat(orderItem.price()).isEqualTo(new Money("50.00"));
    assertThat(orderItem.quantity()).isEqualTo(new Quantity(2));
    assertThat(orderItem.totalAmount()).isEqualTo(new Money("100.00"));
  }

  @Test
  void given_brandNewOrderItem_whenBuild_shouldCalculateTotalFromPriceAndQuantity() {
    OrderId orderId = new OrderId(1L);
    OrderItem orderItem = OrderTestDataBuilder.brandNewOrderItem(orderId)
        .price(new Money("25.50"))
        .quantity(new Quantity(3))
        .build();

    assertThat(orderItem.totalAmount()).isEqualTo(new Money("76.50"));
  }

  @Test
  void given_existingOrderItem_whenBuild_shouldExposeFields() {
    OrderId orderId = new OrderId(1L);
    OrderItem orderItem = OrderTestDataBuilder.existingOrderItem(orderId).build();

    assertThat(orderItem.id()).isNotNull();
    assertThat(orderItem.orderId()).isEqualTo(orderId);
    assertThat(orderItem.productId()).isNotNull();
    assertThat(orderItem.productName()).isEqualTo(new ProductName("Notebook"));
    assertThat(orderItem.price()).isEqualTo(new Money("50.00"));
    assertThat(orderItem.quantity()).isEqualTo(new Quantity(2));
    assertThat(orderItem.totalAmount()).isEqualTo(new Money("100.00"));
  }

  @Test
  void given_orderItemsWithSameId_whenCompare_shouldBeEqual() {
    OrderItemId orderItemId = new OrderItemId(42L);
    OrderId orderId = new OrderId(1L);
    OrderItem first = OrderTestDataBuilder.existingOrderItem(orderId).id(orderItemId).build();
    OrderItem second = OrderTestDataBuilder.existingOrderItem(orderId).id(orderItemId).build();

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_orderItemsWithDifferentId_whenCompare_shouldNotBeEqual() {
    OrderId orderId = new OrderId(1L);
    OrderItem first = OrderTestDataBuilder.existingOrderItem(orderId).id(new OrderItemId(1L)).build();
    OrderItem second = OrderTestDataBuilder.existingOrderItem(orderId).id(new OrderItemId(2L)).build();

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-an-order-item");
  }

  @Test
  void given_nullRequiredFields_whenBuildExistingOrderItem_shouldGenerateException() {
    OrderId orderId = new OrderId(1L);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).id(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).orderId(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).productId(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).productName(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).price(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).quantity(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrderItem(orderId).totalAmount(null).build());
  }

  @Test
  void given_nullRequiredFields_whenBuildBrandNewOrderItem_shouldGenerateException() {
    OrderId orderId = new OrderId(1L);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.brandNewOrderItem(orderId).orderId(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.brandNewOrderItem(orderId).productId(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.brandNewOrderItem(orderId).productName(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.brandNewOrderItem(orderId).price(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.brandNewOrderItem(orderId).quantity(null).build());
  }
}
