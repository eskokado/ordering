package com.eskcti.algashop.ordering.domain.model.order;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.order.Billing;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderCannotBeEditedException;
import com.eskcti.algashop.ordering.domain.model.order.OrderCannotBePlacedException;
import com.eskcti.algashop.ordering.domain.model.order.OrderDoesNotContainOrderItemException;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderInvalidShippingDeliveryDateException;
import com.eskcti.algashop.ordering.domain.model.order.OrderItem;
import com.eskcti.algashop.ordering.domain.model.order.OrderItemId;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.order.Recipient;
import com.eskcti.algashop.ordering.domain.model.order.Shipping;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.commons.ValueObjectTestFixtures;

import org.assertj.core.api.Assertions;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

  @Test
  void given_draftOrder_whenPlace_shouldChangeStatusToPlaced() {
    Order order = OrderTestDataBuilder.anOrder().build();

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
    Order order = OrderTestDataBuilder.anOrder().withItems(false).build();

    Assertions.assertThatExceptionOfType(OrderCannotBePlacedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS, order.id()));
  }

  @Test
  void given_placedOrder_whenPlace_shouldGenerateException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();

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
    Order order = OrderTestDataBuilder.anOrder()
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
    assertThat(order.items()).isEmpty();
  }

  @Test
  void given_draftOrder_whenAddItem_shouldIncludeBrandNewOrderItem() {
    Order order = OrderTestDataBuilder.draftOrder();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    order.addItem(product, quantity);

    assertThat(order.items()).hasSize(1);
    OrderItem addedItem = order.items().iterator().next();
    assertThat(addedItem.orderId()).isEqualTo(order.id());
    assertThat(addedItem.productId()).isEqualTo(product.id());
    assertThat(addedItem.productName()).isEqualTo(product.name());
    assertThat(addedItem.price()).isEqualTo(product.price());
    assertThat(addedItem.quantity()).isEqualTo(quantity);
    assertThat(addedItem.totalAmount()).isEqualTo(new Money("6000.00"));
    assertThat(order.totalAmount()).isEqualTo(new Money("6000.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(2));
  }

  @Test
  void given_draftOrder_whenAddMultipleItems_shouldRecalculateTotals() {
    Order order = OrderTestDataBuilder.draftOrder();

    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
    order.addItem(ProductTestDataBuilder.aProductAltMousePad().build(), new Quantity(1));

    assertThat(order.items()).hasSize(2);
    assertThat(order.totalAmount()).isEqualTo(new Money("6100.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(3));
  }

  @Test
  void given_existingOrderWithShippingCost_whenAddItem_shouldRecalculateTotalsIncludingShipping() {
    Order order = OrderTestDataBuilder.anOrder().build();

    order.addItem(ProductTestDataBuilder.aProductAltMousePad().build(), new Quantity(1));

    assertThat(order.items()).hasSize(3);
    assertThat(order.totalAmount()).isEqualTo(new Money("6310.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(4));
  }

  @Test
  void given_existingOrder_whenAddItem_shouldAppendToExistingItems() {
    Order order = OrderTestDataBuilder.anOrder().build();
    int initialSize = order.items().size();

    order.addItem(ProductTestDataBuilder.aProductAltMousePad().build(), new Quantity(1));

    assertThat(order.items()).hasSize(initialSize + 1);
  }

  @Test
  void given_invalidItemData_whenAddItem_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(null, new Quantity(1)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.addItem(ProductTestDataBuilder.aProduct().build(), null));
  }

  @Test
  void given_outOfStockProduct_whenAddItem_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
        .isThrownBy(() -> order.addItem(ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1)));
  }

  @Test
  void given_order_whenGetItems_shouldReturnUnmodifiableCollection() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

    Assertions.assertThatThrownBy(() -> order.items().add(
        OrderItemTestDataBuilder.brandNewOrderItem(order.id()).build()))
        .isInstanceOf(UnsupportedOperationException.class);

    Assertions.assertThatThrownBy(() -> order.items().clear())
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void given_order_whenAddItemAfterGetItems_shouldReflectUpdatedCollection() {
    Order order = OrderTestDataBuilder.draftOrder();

    assertThat(order.items()).isEmpty();

    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

    assertThat(order.items()).hasSize(1);
  }

  @Test
  void given_existingOrder_whenBuild_shouldExposeFields() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();

    assertThat(order.id()).isNotNull();
    assertThat(order.customerId()).isNotNull();
    assertThat(order.totalAmount()).isEqualTo(new Money("6210.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(3));
    assertThat(order.placedAt()).isNotNull();
    assertThat(order.paidAt()).isNull();
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
    assertThat(order.billing()).isNotNull();
    assertThat(order.shipping()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    assertThat(order.shipping().cost()).isEqualTo(new Money("10.00"));
    assertThat(order.shipping().expectedDate()).isNotNull();
    assertThat(order.items()).hasSize(2);
  }

  @Test
  void given_order_whenChangeShipping_shouldUpdateShipping() {
    Order order = OrderTestDataBuilder.draftOrder();
    Shipping shipping = OrderTestDataBuilder.aShippingAlt();

    order.changeShipping(shipping);

    assertThat(order.shipping()).isEqualTo(shipping);
    assertThat(order.shipping().cost()).isEqualTo(new Money("20.00"));
    assertThat(order.shipping().expectedDate()).isEqualTo(shipping.expectedDate());
  }

  @Test
  void given_order_whenChangeShippingWithPastExpectedDate_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();
    Shipping shipping = Shipping.builder()
        .cost(new Money("25.00"))
        .expectedDate(LocalDate.now().minusDays(1))
        .recipient(Recipient.builder()
            .fullName(ValueObjectTestFixtures.validFullName())
            .document(ValueObjectTestFixtures.validDocument())
            .phone(ValueObjectTestFixtures.validPhone())
            .build())
        .address(OrderTestDataBuilder.anAddress())
        .build();

    Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
        .isThrownBy(() -> order.changeShipping(shipping))
        .withMessage(String.format(ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST, order.id()));
  }

  @Test
  void given_order_whenChangeShippingWithNullRequiredFields_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeShipping(null));
  }

  @Test
  void given_order_whenChangePaymentMethod_shouldUpdateField() {
    Order order = OrderTestDataBuilder.draftOrder();

    order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE);

    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
  }

  @Test
  void given_order_whenChangePaymentMethodWithNull_shouldGenerateException() {
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changePaymentMethod(null));
  }

  @Test
  void given_order_whenChangeBilling_shouldUpdateField() {
    Order order = OrderTestDataBuilder.draftOrder();
    Billing billing = Billing.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .email(ValueObjectTestFixtures.validEmail())
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
    Order order = OrderTestDataBuilder.draftOrder();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeBilling(null));
  }

  @Test
  void given_paidOrder_whenBuild_shouldAllowOptionalTimestamps() {
    OffsetDateTime now = OffsetDateTime.now();
    Order order = Order.existing()
        .id(new OrderId(1L))
        .customerId(new CustomerId())
        .totalAmount(new Money("100.00"))
        .totalItems(new Quantity(2))
        .placedAt(now)
        .paidAt(now)
        .readyAt(now)
        .billing(OrderTestDataBuilder.aBilling())
        .shipping(OrderTestDataBuilder.aShipping())
        .status(OrderStatus.PAID)
        .paymentMethod(PaymentMethod.GATEWAY_BALANCE)
        .items(new HashSet<>())
        .build();

    assertThat(order.paidAt()).isEqualTo(now);
    assertThat(order.readyAt()).isEqualTo(now);
    assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
  }

  @Test
  void given_ordersWithSameId_whenCompare_shouldBeEqual() {
    OrderId orderId = new OrderId(99L);
    Order first = Order.existing()
        .id(orderId)
        .customerId(new CustomerId())
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .billing(OrderTestDataBuilder.aBilling())
        .shipping(OrderTestDataBuilder.aShipping())
        .status(OrderStatus.DRAFT)
        .items(new HashSet<>())
        .build();
    Order second = Order.existing()
        .id(orderId)
        .customerId(new CustomerId())
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .billing(OrderTestDataBuilder.aBilling())
        .shipping(OrderTestDataBuilder.aShipping())
        .status(OrderStatus.DRAFT)
        .items(new HashSet<>())
        .build();

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_ordersWithDifferentId_whenCompare_shouldNotBeEqual() {
    Order first = Order.existing()
        .id(new OrderId(1L))
        .customerId(new CustomerId())
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .billing(OrderTestDataBuilder.aBilling())
        .shipping(OrderTestDataBuilder.aShipping())
        .status(OrderStatus.DRAFT)
        .items(new HashSet<>())
        .build();
    Order second = Order.existing()
        .id(new OrderId(2L))
        .customerId(new CustomerId())
        .totalAmount(Money.ZERO)
        .totalItems(Quantity.ZERO)
        .billing(OrderTestDataBuilder.aBilling())
        .shipping(OrderTestDataBuilder.aShipping())
        .status(OrderStatus.DRAFT)
        .items(new HashSet<>())
        .build();

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
        .isThrownBy(() -> Order.existing().id(null).customerId(new CustomerId()).totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO).status(OrderStatus.DRAFT).items(new HashSet<>()).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.existing().id(new OrderId(1L)).customerId(null).totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO).status(OrderStatus.DRAFT).items(new HashSet<>()).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.existing().id(new OrderId(1L)).customerId(new CustomerId()).totalAmount(null)
            .totalItems(Quantity.ZERO).status(OrderStatus.DRAFT).items(new HashSet<>()).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.existing().id(new OrderId(1L)).customerId(new CustomerId()).totalAmount(Money.ZERO)
            .totalItems(null).status(OrderStatus.DRAFT).items(new HashSet<>()).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.existing().id(new OrderId(1L)).customerId(new CustomerId()).totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO).status(null).items(new HashSet<>()).build());

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Order.existing().id(new OrderId(1L)).customerId(new CustomerId()).totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO).status(OrderStatus.DRAFT).items(null).build());
  }

  @Test
  void given_placedOrder_whenMarkAsPaid_shouldSetPaidAtAndStatusToPaid() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    assertThat(order.isPaid()).isFalse();
    assertThat(order.paidAt()).isNull();

    order.markAsPaid();

    assertThat(order.isPaid()).isTrue();
    assertThat(order.paidAt()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.PAID);
  }

  @Test
  void given_draftOrder_whenIsPaid_shouldReturnFalse() {
    Order order = OrderTestDataBuilder.draftOrder();
    assertThat(order.isPaid()).isFalse();
  }

  @Test
  void given_draftOrderWithoutShippingInfo_whenPlace_shouldGenerateException() {
    Order order = aPlaceableDraftOrderWithoutShipping();

    Assertions.assertThatExceptionOfType(OrderCannotBePlacedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, order.id()));
  }

  @Test
  void given_draftOrderWithoutBillingInfo_whenPlace_shouldGenerateException() {
    Order order = aPlaceableDraftOrderWithoutBilling();

    Assertions.assertThatExceptionOfType(OrderCannotBePlacedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, order.id()));
  }

  @Test
  void given_draftOrderWithoutPaymentMethod_whenPlace_shouldGenerateException() {
    Order order = aPlaceableDraftOrderWithoutPaymentMethod();

    Assertions.assertThatExceptionOfType(OrderCannotBePlacedException.class)
        .isThrownBy(order::place)
        .withMessage(String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, order.id()));
  }

  @Test
  void given_orderWithItem_whenChangeItemQuantity_shouldUpdateQuantityAndRecalculateTotals() {
    Order order = aDraftOrderWithSingleItem();

    OrderItem item = order.items().iterator().next();
    assertThat(item.quantity()).isEqualTo(new Quantity(2));
    assertThat(order.totalAmount()).isEqualTo(new Money("110.00"));

    order.changeItemQuantity(item.id(), new Quantity(5));

    assertThat(item.quantity()).isEqualTo(new Quantity(5));
    assertThat(order.totalAmount()).isEqualTo(new Money("260.00"));
  }

  @Test
  void given_orderWithItem_whenChangeItemQuantityWithNulls_shouldThrowNullPointerException() {
    Order order = aDraftOrderWithSingleItem();
    OrderItem item = order.items().iterator().next();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeItemQuantity(null, new Quantity(5)));

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.changeItemQuantity(item.id(), null));
  }

  @Test
  void given_orderWithoutItem_whenChangeItemQuantity_shouldThrowOrderDoesNotContainOrderItemException() {
    Order order = OrderTestDataBuilder.draftOrder();
    OrderItemId nonExistingItemId = new OrderItemId(999L);

    Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
        .isThrownBy(() -> order.changeItemQuantity(nonExistingItemId, new Quantity(5)))
        .withMessage(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, order.id(), nonExistingItemId));
  }

  private Order aDraftOrderWithSingleItem() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.changeShipping(OrderTestDataBuilder.aShipping());
    order.changeBilling(OrderTestDataBuilder.aBilling());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    order.addItem(OrderItemTestDataBuilder.validProduct(), OrderItemTestDataBuilder.validQuantity());
    return order;
  }

  private Order aPlaceableDraftOrderWithoutShipping() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
    order.changeBilling(OrderTestDataBuilder.aBilling());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    return order;
  }

  private Order aPlaceableDraftOrderWithoutBilling() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
    order.changeShipping(OrderTestDataBuilder.aShipping());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    return order;
  }

  private Order aPlaceableDraftOrderWithoutPaymentMethod() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
    order.changeBilling(OrderTestDataBuilder.aBilling());
    order.changeShipping(OrderTestDataBuilder.aShipping());
    return order;
  }

  @Test
  void given_placedOrder_whenMarkAsReady_shouldSetReadyAtAndStatusToReady() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.markAsPaid();
    assertThat(order.isReady()).isFalse();
    assertThat(order.readyAt()).isNull();

    order.markAsReady();

    assertThat(order.isReady()).isTrue();
    assertThat(order.readyAt()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.READY);
  }

  @Test
  void given_placedOrder_whenCancel_shouldSetCanceledAtAndStatusToCanceled() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    assertThat(order.isCanceled()).isFalse();
    assertThat(order.canceledAt()).isNull();

    order.cancel();

    assertThat(order.isCanceled()).isTrue();
    assertThat(order.canceledAt()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
  }

  @Test
  void given_paidOrder_whenCancel_shouldSetCanceledAtAndStatusToCanceled() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.markAsPaid();
    assertThat(order.isCanceled()).isFalse();
    assertThat(order.canceledAt()).isNull();

    order.cancel();

    assertThat(order.isCanceled()).isTrue();
    assertThat(order.canceledAt()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
  }

  @Test
  void given_readyOrder_whenCancel_shouldSetCanceledAtAndStatusToCanceled() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.markAsPaid();
    order.markAsReady();
    assertThat(order.isCanceled()).isFalse();
    assertThat(order.canceledAt()).isNull();

    order.cancel();

    assertThat(order.isCanceled()).isTrue();
    assertThat(order.canceledAt()).isNotNull();
    assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
  }

  @Test
  void given_draftOrderWithItem_whenRemoveItem_shouldRemoveItemAndRecalculateTotals() {
    Order order = OrderTestDataBuilder.draftOrder();
    order.addItem(OrderItemTestDataBuilder.validProduct(), OrderItemTestDataBuilder.validQuantity());
    OrderItem item = order.items().iterator().next();

    order.removeItem(item.id());

    assertThat(order.items()).isEmpty();
    assertThat(order.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(order.totalItems()).isEqualTo(Quantity.ZERO);
  }

  @Test
  void given_draftOrder_whenRemoveItemWithNullOrderItemId_shouldThrowNullPointerException() {
    Order order = aDraftOrderWithSingleItem();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> order.removeItem(null));
  }

  @Test
  void given_draftOrderWithoutItem_whenRemoveItem_shouldThrowOrderDoesNotContainOrderItemException() {
    Order order = OrderTestDataBuilder.draftOrder();
    OrderItemId nonExistingItemId = new OrderItemId(999L);

    Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
        .isThrownBy(() -> order.removeItem(nonExistingItemId))
        .withMessage(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, order.id(), nonExistingItemId));
  }

  @Test
  void given_placedOrder_whenAddItem_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(1);

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.addItem(product, quantity));
  }

  @Test
  void given_paidOrder_whenAddItem_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.markAsPaid();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(1);

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.addItem(product, quantity));
  }

  @Test
  void given_readyOrder_whenAddItem_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.markAsPaid();
    order.markAsReady();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(1);

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.addItem(product, quantity));
  }

  @Test
  void given_canceledOrder_whenAddItem_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    order.cancel();
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(1);

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.addItem(product, quantity));
  }

  @Test
  void given_placedOrder_whenChangePaymentMethod_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE));
  }

  @Test
  void given_placedOrder_whenChangeBilling_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    Billing billing = OrderTestDataBuilder.aBilling();

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.changeBilling(billing));
  }

  @Test
  void given_placedOrder_whenChangeShipping_shouldThrowOrderCannotBeEditedException() {
    Order order = OrderTestDataBuilder.aPlacedOrder().build();
    Shipping shipping = OrderTestDataBuilder.aShipping();

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.changeShipping(shipping));
  }

  @Test
  void given_placedOrder_whenChangeItemQuantity_shouldThrowOrderCannotBeEditedException() {
    Order order = aDraftOrderWithSingleItem();
    order.changeShipping(OrderTestDataBuilder.aShipping());
    order.changeBilling(OrderTestDataBuilder.aBilling());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    order.place();
    OrderItem item = order.items().iterator().next();

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.changeItemQuantity(item.id(), new Quantity(5)));
  }

  @Test
  void given_placedOrder_whenRemoveItem_shouldThrowOrderCannotBeEditedException() {
    Order order = aDraftOrderWithSingleItem();
    order.changeShipping(OrderTestDataBuilder.aShipping());
    order.changeBilling(OrderTestDataBuilder.aBilling());
    order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
    order.place();
    OrderItem item = order.items().iterator().next();

    Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
        .isThrownBy(() -> order.removeItem(item.id()));
  }

  @Test
  void given_draftOrderWithItemsNull_whenAddItem_shouldInitializeItemsSet() throws Exception {
    Order order = OrderTestDataBuilder.draftOrder();
    Field itemsField = Order.class.getDeclaredField("items");
    itemsField.setAccessible(true);
    itemsField.set(order, null);
    Product product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(1);

    order.addItem(product, quantity);

    assertThat(order.items()).hasSize(1);
    assertThat(order.totalAmount()).isEqualTo(new Money("3000.00"));
    assertThat(order.totalItems()).isEqualTo(new Quantity(1));
  }
}
