package com.eskcti.algashop.ordering.domain.model.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductOutOfStockException;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

  @InjectMocks
  private BuyNowService buyNowService;

  @Mock
  private Orders orders;

  @Test
  void givenValidProductAndDetails_whenBuyNow_shouldReturnPlacedOrder() {
    Product product = ProductTestDataBuilder.aProduct().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(3);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    Order order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

    assertThat(order).isNotNull();
    assertThat(order.id()).isNotNull();
    assertThat(order.customerId()).isEqualTo(customer.id());
    assertThat(order.billing()).isEqualTo(billingInfo);
    assertThat(order.shipping()).isEqualTo(shippingInfo);
    assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
    assertThat(order.isPlaced()).isTrue();

    assertThat(order.items()).hasSize(1);

    OrderItem orderItem = order.items().iterator().next();
    assertThat(orderItem.productId()).isEqualTo(product.id());
    assertThat(orderItem.quantity()).isEqualTo(quantity);
    assertThat(orderItem.price()).isEqualTo(product.price());
    assertThat(orderItem.totalAmount()).isEqualTo(product.price().multiply(quantity));

    Money expectedTotalAmount = product.price().multiply(quantity).add(shippingInfo.cost());
    assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
    assertThat(order.totalItems()).isEqualTo(quantity);
  }

  @Test
  void givenCustomerEligibleForFreeShippingByLoyaltyAndSales_whenBuyNow_shouldApplyFreeShipping() {
    Product product = ProductTestDataBuilder.aProduct().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .loyaltyPoints(new LoyaltyPoints(100))
        .build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(1);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    when(orders.salesQuantityByCustomerInYear(eq(customer.id()), eq(Year.now()))).thenReturn(2L);

    Order order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

    assertThat(order.shipping().cost()).isEqualTo(Money.ZERO);
    assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity));
  }

  @Test
  void givenCustomerWithVeryHighLoyalty_whenBuyNow_shouldApplyFreeShipping() {
    Product product = ProductTestDataBuilder.aProduct().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .loyaltyPoints(new LoyaltyPoints(2000))
        .build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(1);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    when(orders.salesQuantityByCustomerInYear(eq(customer.id()), eq(Year.now()))).thenReturn(0L);

    Order order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

    assertThat(order.shipping().cost()).isEqualTo(Money.ZERO);
    assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity));
  }

  @Test
  void givenCustomerWithModerateLoyaltyAndInsufficientSales_whenBuyNow_shouldChargeShipping() {
    Product product = ProductTestDataBuilder.aProduct().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .loyaltyPoints(new LoyaltyPoints(150))
        .build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(1);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    when(orders.salesQuantityByCustomerInYear(eq(customer.id()), eq(Year.now()))).thenReturn(1L);

    Order order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

    assertThat(order.shipping().cost()).isEqualTo(shippingInfo.cost());
    assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity).add(shippingInfo.cost()));
  }

  @Test
  void givenOutOfStockProduct_whenBuyNow_shouldThrowProductOutOfStockException() {
    Product product = ProductTestDataBuilder.aProductUnavailable().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(1);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    assertThatExceptionOfType(ProductOutOfStockException.class)
        .isThrownBy(
            () -> buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod));
  }

  @Test
  void givenInvalidQuantity_whenBuyNow_shouldThrowIllegalArgumentException() {
    Product product = ProductTestDataBuilder.aProduct().build();
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    Quantity quantity = new Quantity(0);
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod));
  }

}