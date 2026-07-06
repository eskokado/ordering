package com.eskcti.algashop.ordering.domain.entity;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

  @Test
  void given_draftOrder_whenCreate_shouldInitializeDefaults() {
    Order order = OrderTestDataBuilder.draftOrder();

    assertThat(order.id()).isNotNull();
    assertThat(order.customerId()).isNotNull();
    assertThat(order.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(order.totalItems()).isEqualTo(Quantity.ZERO);
    assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
    assertThat(order.placedAt()).isNull();
    assertThat(order.paidAt()).isNull();
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
    assertThat(order.billing()).isNull();
    assertThat(order.shipping()).isNull();
    assertThat(order.paymentMethod()).isNull();
    assertThat(order.shippingCost()).isNull();
    assertThat(order.expectedDeliveryDate()).isNull();
    assertThat(order.items()).isEmpty();
  }

  @Test
  void given_draftOrder_whenAddItem_shouldIncludeBrandNewOrderItem() {
    Order order = OrderTestDataBuilder.draftOrder();
    ProductId productId = OrderTestDataBuilder.validProductId();
    ProductName productName = OrderTestDataBuilder.validProductName();
    Money price = OrderTestDataBuilder.validPrice();
    Quantity quantity = OrderTestDataBuilder.validQuantity();

    order.addItem(productId, productName, price, quantity);

    assertThat(order.items()).hasSize(1);
    OrderItem addedItem = order.items().iterator().next();
    assertThat(addedItem.orderId()).isEqualTo(order.id());
    assertThat(addedItem.productId()).isEqualTo(productId);
    assertThat(addedItem.productName()).isEqualTo(productName);
    assertThat(addedItem.price()).isEqualTo(price);
    assertThat(addedItem.quantity()).isEqualTo(quantity);
    assertThat(addedItem.totalAmount()).isEqualTo(Money.ZERO);
  }

  @Test
  void given_draftOrder_whenAddMultipleItems_shouldIncreaseItemsCollection() {
    Order order = OrderTestDataBuilder.draftOrder();

    order.addItem(
        OrderTestDataBuilder.validProductId(),
        OrderTestDataBuilder.validProductName(),
        OrderTestDataBuilder.validPrice(),
        OrderTestDataBuilder.validQuantity());
    order.addItem(
        OrderTestDataBuilder.validProductId(),
        new ProductName("Mouse"),
        new Money("25.00"),
        new Quantity(1));

    assertThat(order.items()).hasSize(2);
  }

  @Test
  void given_existingOrder_whenAddItem_shouldAppendToExistingItems() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    order.addItem(
        OrderTestDataBuilder.validProductId(),
        new ProductName("Keyboard"),
        new Money("120.00"),
        new Quantity(1));

    assertThat(order.items()).hasSize(2);
  }

  @Test
  void given_invalidItemData_whenAddItem_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(null, OrderTestDataBuilder.validProductName(),
            OrderTestDataBuilder.validPrice(), OrderTestDataBuilder.validQuantity()));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(OrderTestDataBuilder.validProductId(), null,
            OrderTestDataBuilder.validPrice(), OrderTestDataBuilder.validQuantity()));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(OrderTestDataBuilder.validProductId(),
            OrderTestDataBuilder.validProductName(), null, OrderTestDataBuilder.validQuantity()));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(OrderTestDataBuilder.validProductId(),
            OrderTestDataBuilder.validProductName(), OrderTestDataBuilder.validPrice(), null));
  }

  @Test
  void given_existingOrder_whenBuild_shouldExposeFields() {
    Order order = OrderTestDataBuilder.existingOrder().build();

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
  void given_paidOrder_whenBuild_shouldAllowOptionalTimestamps() {
    OffsetDateTime now = OffsetDateTime.now();
    Order order = OrderTestDataBuilder.existingOrder()
        .paidAt(now)
        .readyAt(now)
        .status(OrderStatus.PAID)
        .paymentMethod(PaymentMethod.GATEWAY_BALANCE)
        .build();

    assertThat(order.paidAt()).isEqualTo(now);
    assertThat(order.readyAt()).isEqualTo(now);
    assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
  }

  @Test
  void given_ordersWithSameId_whenCompare_shouldBeEqual() {
    OrderId orderId = new OrderId(99L);
    Order first = OrderTestDataBuilder.existingOrder().id(orderId).build();
    Order second = OrderTestDataBuilder.existingOrder().id(orderId).build();

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_ordersWithDifferentId_whenCompare_shouldNotBeEqual() {
    Order first = OrderTestDataBuilder.existingOrder().id(new OrderId(1L)).build();
    Order second = OrderTestDataBuilder.existingOrder().id(new OrderId(2L)).build();

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-an-order");
  }

  @Test
  void given_nullCustomerId_whenCreateDraft_shouldGenerateException() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.draft(null));
  }

  @Test
  void given_nullRequiredFields_whenBuildExistingOrder_shouldGenerateException() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().id(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().customerId(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().totalAmount(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().totalItems(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().status(null).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderTestDataBuilder.existingOrder().items(null).build());
  }
}
