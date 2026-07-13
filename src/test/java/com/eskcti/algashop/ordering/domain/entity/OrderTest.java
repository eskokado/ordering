package com.eskcti.algashop.ordering.domain.entity;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.exception.OrderCannotBePlacedException;
import com.eskcti.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.eskcti.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.eskcti.algashop.ordering.domain.valueobject.BillingInfo;
import com.eskcti.algashop.ordering.domain.valueobject.Money;
import com.eskcti.algashop.ordering.domain.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.valueobject.ShippingInfo;
import com.eskcti.algashop.ordering.domain.valueobject.ValueObjectTestFixtures;
import com.eskcti.algashop.ordering.domain.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.valueobject.id.ProductId;

import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NOT_ITEMS;
import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST;
import static com.eskcti.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

  @Test
  void given_draftOrder_whenPlace_shouldChangeStatusToPlaced() {
    Order order = aPlaceableDraftOrder();

    assertThat(order.isDraft()).isTrue();
    assertThat(order.isPlaced()).isFalse();
    assertThat(order.placedAt()).isNull();

    order.place();

    assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    assertThat(order.isDraft()).isFalse();
    assertThat(order.isPlaced()).isTrue();
    assertThat(order.placedAt()).isNotNull();
  }

  @Test
  void given_draftOrderWithoutItems_whenPlace_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.changeBilling(OrderTestDataBuilder.existingOrder().build().billing());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    order.changeShipping(
        OrderTestDataBuilder.existingOrder().build().shipping(),
        new Money("10.00"),
        LocalDate.now().plusDays(5));

    Assertions.assertThatExceptionOfType(OrderCannotBePlacedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NOT_ITEMS, order.id()));
  }

  @Test
  void given_placedOrder_whenPlace_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(
            ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,
            order.id(),
            OrderStatus.PLACED,
            OrderStatus.PLACED));
  }

  @Test
  void given_canceledOrder_whenPlace_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder()
        .status(OrderStatus.CANCELED)
        .build();

    Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(
            ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,
            order.id(),
            OrderStatus.CANCELED,
            OrderStatus.PLACED));
  }

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
    assertThat(addedItem.totalAmount()).isEqualTo(new Money("100.00"));
    assertThat(order.totalAmount()).isEqualTo(new Money("100.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(2));
  }

  @Test
  void given_draftOrder_whenAddMultipleItems_shouldRecalculateTotals() {
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
    assertThat(order.totalAmount()).isEqualTo(new Money("125.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(3));
  }

  @Test
  void given_existingOrderWithShippingCost_whenAddItem_shouldRecalculateTotalsIncludingShipping() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    order.addItem(
        OrderTestDataBuilder.validProductId(),
        new ProductName("Keyboard"),
        new Money("120.00"),
        new Quantity(1));

    assertThat(order.items()).hasSize(2);
    assertThat(order.totalAmount()).isEqualTo(new Money("230.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(3));
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
  void given_order_whenGetItems_shouldReturnUnmodifiableCollection() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(
        OrderTestDataBuilder.validProductId(),
        OrderTestDataBuilder.validProductName(),
        OrderTestDataBuilder.validPrice(),
        OrderTestDataBuilder.validQuantity());

    Assertions.assertThatThrownBy(() -> order.items().add(
        OrderTestDataBuilder.brandNewOrderItem(order.id()).build()))
        .isInstanceOf(UnsupportedOperationException.class);

    Assertions.assertThatThrownBy(() -> order.items().clear())
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void given_order_whenAddItemAfterGetItems_shouldReflectUpdatedCollection() {
    Order order = OrderTestDataBuilder.draftOrder();

    assertThat(order.items()).isEmpty();

    order.addItem(
        OrderTestDataBuilder.validProductId(),
        OrderTestDataBuilder.validProductName(),
        OrderTestDataBuilder.validPrice(),
        OrderTestDataBuilder.validQuantity());

    assertThat(order.items()).hasSize(1);
  }

  @Test
  void given_orderWithNullItems_whenAddItem_shouldInitializeCollection() throws Exception {
    Order order = OrderTestDataBuilder.draftOrder();
    Field itemsField = Order.class.getDeclaredField("items");
    itemsField.setAccessible(true);
    itemsField.set(order, null);

    order.addItem(
        OrderTestDataBuilder.validProductId(),
        OrderTestDataBuilder.validProductName(),
        OrderTestDataBuilder.validPrice(),
        OrderTestDataBuilder.validQuantity());

    assertThat(order.items()).hasSize(1);
    assertThat(order.totalAmount()).isEqualTo(new Money("100.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(2));
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
  void given_order_whenChangeShipping_shouldUpdateFieldsAndRecalculateTotal() {
    Order order = OrderTestDataBuilder.existingOrder().build();
    ShippingInfo shipping = ShippingInfo.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .address(ValueObjectTestFixtures.validAddress().toBuilder()
            .street("Shipping Street")
            .number("999")
            .build())
        .build();
    Money shippingCost = new Money("25.00");
    LocalDate expectedDeliveryDate = LocalDate.now().plusDays(7);

    order.changeShipping(shipping, shippingCost, expectedDeliveryDate);

    assertThat(order.shipping()).isEqualTo(shipping);
    assertThat(order.shippingCost()).isEqualTo(shippingCost);
    assertThat(order.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate);
    assertThat(order.totalAmount()).isEqualTo(new Money("125.00"));
  }

  @Test
  void given_order_whenChangeShippingWithPastExpectedDate_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder().build();
    ShippingInfo shipping = OrderTestDataBuilder.existingOrder().build().shipping();
    LocalDate expectedDeliveryDate = LocalDate.now().minusDays(1);

    Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
        .isThrownBy(() -> order.changeShipping(shipping, new Money("25.00"), expectedDeliveryDate))
        .withMessage(String.format(ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST, order.id()));
  }

  @Test
  void given_order_whenChangeShippingWithNullRequiredFields_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder().build();
    ShippingInfo shipping = OrderTestDataBuilder.existingOrder().build().shipping();
    Money shippingCost = new Money("25.00");
    LocalDate expectedDeliveryDate = LocalDate.now().plusDays(7);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeShipping(null, shippingCost, expectedDeliveryDate));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeShipping(shipping, null, expectedDeliveryDate));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeShipping(shipping, shippingCost, null));
  }

  @Test
  void given_order_whenChangePaymentMethod_shouldUpdateField() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE);

    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
  }

  @Test
  void given_order_whenChangePaymentMethodWithNull_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changePaymentMethod(null));
  }

  @Test
  void given_order_whenChangeBilling_shouldUpdateField() {
    Order order = OrderTestDataBuilder.existingOrder().build();
    BillingInfo billing = BillingInfo.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .address(ValueObjectTestFixtures.validAddress().toBuilder()
            .street("Billing Street")
            .number("123")
            .build())
        .build();

    order.changeBilling(billing);

    assertThat(order.billing()).isEqualTo(billing);
  }

  @Test
  void given_order_whenChangeBillingWithNull_shouldGenerateException() {
    Order order = OrderTestDataBuilder.existingOrder().build();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeBilling(null));
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

  private Order aPlaceableDraftOrder() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(
        OrderTestDataBuilder.validProductId(),
        OrderTestDataBuilder.validProductName(),
        OrderTestDataBuilder.validPrice(),
        OrderTestDataBuilder.validQuantity());
    order.changeBilling(OrderTestDataBuilder.existingOrder().build().billing());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    order.changeShipping(
        OrderTestDataBuilder.existingOrder().build().shipping(),
        new Money("10.00"),
        LocalDate.now().plusDays(5));
    return order;
  }
}
