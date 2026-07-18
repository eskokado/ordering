package com.eskcti.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Billing;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderFactory;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.order.Shipping;

class OrderFactoryTest {

  @Test
  void shouldCreateFilledOrderSuccessfully() {
    CustomerId customerId = new CustomerId();
    Shipping shipping = OrderTestDataBuilder.aShipping();
    Billing billing = OrderTestDataBuilder.aBilling();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    var product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    Order order = OrderFactory.filled(customerId, shipping, billing, paymentMethod, product, quantity);

    Assertions.assertThat(order).isNotNull();
    Assertions.assertThat(order.customerId()).isEqualTo(customerId);
    Assertions.assertThat(order.shipping()).isEqualTo(shipping);
    Assertions.assertThat(order.billing()).isEqualTo(billing);
    Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
    Assertions.assertThat(order.items()).hasSize(1);
    Assertions.assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
  }

  @Test
  void shouldThrowNullPointerExceptionWhenCustomerIdIsNull() {
    Shipping shipping = OrderTestDataBuilder.aShipping();
    Billing billing = OrderTestDataBuilder.aBilling();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    var product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(null, shipping, billing, paymentMethod, product, quantity));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenShippingIsNull() {
    CustomerId customerId = new CustomerId();
    Billing billing = OrderTestDataBuilder.aBilling();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    var product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(customerId, null, billing, paymentMethod, product, quantity));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenBillingIsNull() {
    CustomerId customerId = new CustomerId();
    Shipping shipping = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    var product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(customerId, shipping, null, paymentMethod, product, quantity));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenPaymentMethodIsNull() {
    CustomerId customerId = new CustomerId();
    Shipping shipping = OrderTestDataBuilder.aShipping();
    Billing billing = OrderTestDataBuilder.aBilling();
    var product = ProductTestDataBuilder.aProduct().build();
    Quantity quantity = new Quantity(2);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(customerId, shipping, billing, null, product, quantity));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenProductIsNull() {
    CustomerId customerId = new CustomerId();
    Shipping shipping = OrderTestDataBuilder.aShipping();
    Billing billing = OrderTestDataBuilder.aBilling();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    Quantity quantity = new Quantity(2);

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(customerId, shipping, billing, paymentMethod, null, quantity));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenProductQuantityIsNull() {
    CustomerId customerId = new CustomerId();
    Shipping shipping = OrderTestDataBuilder.aShipping();
    Billing billing = OrderTestDataBuilder.aBilling();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    var product = ProductTestDataBuilder.aProduct().build();

    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> OrderFactory.filled(customerId, shipping, billing, paymentMethod, product, null));
  }
}
