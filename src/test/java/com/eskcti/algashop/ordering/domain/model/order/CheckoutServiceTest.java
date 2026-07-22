package com.eskcti.algashop.ordering.domain.model.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

  private CheckoutService checkoutService;

  @Mock
  private Orders orders;

  @BeforeEach
  void setup() {
    var specification = new CustomerHaveFreeShippingSpecification(
        orders,
        new LoyaltyPoints(100),
        2L,
        new LoyaltyPoints(2000));
    checkoutService = new CheckoutService(specification);
  }

  @Test
  void givenValidShoppingCart_whenCheckout_shouldReturnPlacedOrderAndEmptyShoppingCart() {
    CustomerId customerId = new CustomerId();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .id(customerId)
        .build();
    ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
    Product product1 = ProductTestDataBuilder.aProduct().build();
    Product product2 = ProductTestDataBuilder.aProductAltRamMemory().build();
    shoppingCart.addItem(product1, new Quantity(2));
    shoppingCart.addItem(product2, new Quantity(1));

    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    Money shoppingCartTotalAmount = shoppingCart.totalAmount();
    Quantity expectedOrderTotalItems = shoppingCart.totalItems();
    int expectedOrderItemsCount = shoppingCart.items().size();

    Order order = checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod);

    assertThat(order).isNotNull();
    assertThat(order.id()).isNotNull();
    assertThat(order.customerId()).isEqualTo(customerId);
    assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
    assertThat(order.billing()).isEqualTo(billingInfo);
    assertThat(order.shipping()).isEqualTo(shippingInfo);
    assertThat(order.isPlaced()).isTrue();

    Money expectedTotalAmountWithShipping = shoppingCartTotalAmount.add(shippingInfo.cost());
    assertThat(order.totalAmount()).isEqualTo(expectedTotalAmountWithShipping);
    assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
    assertThat(order.items()).hasSize(expectedOrderItemsCount);

    OrderItem firstOrderItem = order.items().stream()
        .filter(item -> item.productId().equals(product1.id()))
        .findFirst()
        .orElseThrow();
    OrderItem secondOrderItem = order.items().stream()
        .filter(item -> item.productId().equals(product2.id()))
        .findFirst()
        .orElseThrow();

    assertThat(firstOrderItem.quantity()).isEqualTo(new Quantity(2));
    assertThat(firstOrderItem.price()).isEqualTo(product1.price());
    assertThat(firstOrderItem.totalAmount()).isEqualTo(product1.price().multiply(new Quantity(2)));

    assertThat(secondOrderItem.quantity()).isEqualTo(new Quantity(1));
    assertThat(secondOrderItem.price()).isEqualTo(product2.price());
    assertThat(secondOrderItem.totalAmount()).isEqualTo(product2.price());

    assertThat(shoppingCart.isEmpty()).isTrue();
    assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
  }

  @Test
  void givenCustomerEligibleForFreeShipping_whenCheckout_shouldApplyFreeShipping() {
    CustomerId customerId = new CustomerId();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .id(customerId)
        .loyaltyPoints(new LoyaltyPoints(2000))
        .build();
    ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
    Product product = ProductTestDataBuilder.aProduct().build();
    shoppingCart.addItem(product, new Quantity(1));

    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    when(orders.salesQuantityByCustomerInYear(eq(customer.id()), eq(Year.now()))).thenReturn(0L);

    Order order = checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod);

    assertThat(order.shipping().cost()).isEqualTo(Money.ZERO);
    assertThat(order.totalAmount()).isEqualTo(product.price());
    assertThat(shoppingCart.isEmpty()).isTrue();
  }

  @Test
  void givenShoppingCartWithUnavailableItems_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
    Product product = ProductTestDataBuilder.aProduct().build();
    shoppingCart.addItem(product, new Quantity(1));

    Product productUnavailable = ProductTestDataBuilder.aProduct().id(product.id()).inStock(false).build();
    shoppingCart.refreshItem(productUnavailable);

    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
        .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod));

    assertThat(shoppingCart.isEmpty()).isFalse();
    assertThat(shoppingCart.items()).hasSize(1);
  }

  @Test
  void givenEmptyShoppingCart_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
        .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod));

    assertThat(shoppingCart.isEmpty()).isTrue();
  }

  @Test
  void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
    CustomerId customerId = new CustomerId();
    Customer customer = CustomerTestDataBuilder.existingCustomer()
        .id(customerId)
        .build();
    ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
    Product productInStock = ProductTestDataBuilder.aProduct().build();
    shoppingCart.addItem(productInStock, new Quantity(2));

    Product productAlt = ProductTestDataBuilder.aProductAltRamMemory().build();
    shoppingCart.addItem(productAlt, new Quantity(1));

    Product productAltUnavailable = ProductTestDataBuilder.aProductAltRamMemory().id(productAlt.id()).inStock(false)
        .build();
    shoppingCart.refreshItem(productAltUnavailable);

    Billing billingInfo = OrderTestDataBuilder.aBilling();
    Shipping shippingInfo = OrderTestDataBuilder.aShipping();
    PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
        .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod));

    assertThat(shoppingCart.isEmpty()).isFalse();

    Money expectedTotalAmount = productInStock.price()
        .multiply(new Quantity(2))
        .add(productAlt.price());
    assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
    assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
    assertThat(shoppingCart.items()).hasSize(2);
  }
}
