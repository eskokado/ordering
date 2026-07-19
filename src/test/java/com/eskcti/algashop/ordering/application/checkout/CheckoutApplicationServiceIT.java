package com.eskcti.algashop.ordering.application.checkout;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.order.CheckoutService;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.Orders;
import com.eskcti.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;

@SpringBootTest
@Transactional
class CheckoutApplicationServiceIT {

  @Autowired
  private CheckoutApplicationService service;

  @Autowired
  private Orders orders;

  @Autowired
  private ShoppingCarts shoppingCarts;

  @Autowired
  private Customers customers;

  @Autowired
  private CheckoutService checkoutService;

  @Autowired
  private OriginAddressService originAddressService;

  @MockitoBean(name = "shippingCostServiceRapidexImpl")
  private ShippingCostService shippingCostService;

  @BeforeEach
  public void setup() {
    Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
        .thenReturn(new ShippingCostService.CalculationResult(
            new Money("10.00"),
            LocalDate.now().plusDays(3)));

    if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
      customers.add(CustomerTestDataBuilder
          .existingCustomer()
          .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
          .build());
    }
  }

  @Test
  void shouldCheckout() {
    Product product = ProductTestDataBuilder.aProduct().inStock(true).build();

    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder
        .aShoppingCart()
        .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
        .withItems(false).build();
    shoppingCart.addItem(product, new Quantity(1));
    shoppingCarts.add(shoppingCart);

    CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
        .shoppingCartId(shoppingCart.id().value())
        .build();

    String orderId = service.checkout(input);

    Assertions.assertThat(orderId).isNotBlank();
    Assertions.assertThat(orders.exists(new OrderId(orderId))).isTrue();

    Optional<Order> createdOrder = orders.ofId(new OrderId(orderId));
    Assertions.assertThat(createdOrder).isPresent();
    Assertions.assertThat(createdOrder.get().status()).isEqualTo(OrderStatus.PLACED);
    Assertions.assertThat(createdOrder.get().totalAmount().value()).isGreaterThan(BigDecimal.ZERO);

    Optional<ShoppingCart> updatedCart = shoppingCarts.ofId(shoppingCart.id());
    Assertions.assertThat(updatedCart).isPresent();
    Assertions.assertThat(updatedCart.get().isEmpty()).isTrue();
  }

  @Test
  void shouldThrowShoppingCartNotFoundExceptionWhenCheckoutWithNonExistingShoppingCart() {
    CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
        .shoppingCartId(UUID.randomUUID())
        .build();

    Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
        .isThrownBy(() -> service.checkout(input));
  }

  @Test
  void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartIsEmpty() {
    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder
        .aShoppingCart()
        .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
        .withItems(false).build();
    shoppingCarts.add(shoppingCart);

    CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
        .shoppingCartId(shoppingCart.id().value())
        .build();

    Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
        .isThrownBy(() -> service.checkout(input));
  }

  @Test
  void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartContainsUnavailableItems() {
    Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
    Product unavailableProduct = ProductTestDataBuilder.aProduct().id(product.id()).inStock(false).build();

    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder
        .aShoppingCart()
        .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
        .withItems(false).build();
    shoppingCart.addItem(product, new Quantity(1));
    shoppingCart.refreshItem(unavailableProduct);
    shoppingCarts.add(shoppingCart);

    CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
        .shoppingCartId(shoppingCart.id().value())
        .build();

    Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
        .isThrownBy(() -> service.checkout(input));
  }
}