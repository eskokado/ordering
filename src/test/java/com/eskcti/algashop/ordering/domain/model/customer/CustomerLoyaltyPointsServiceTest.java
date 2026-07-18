package com.eskcti.algashop.ordering.domain.model.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CantAddLoyaltyPointsOrderIsNotReady;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerLoyaltyPointsService;
import com.eskcti.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.product.Product;

class CustomerLoyaltyPointsServiceTest {

  CustomerLoyaltyPointsService customerLoyaltyPointsService = new CustomerLoyaltyPointsService();

  @Test
  public void givenValidCustomerAndOrder_WhenAddingPoints_ShouldAccumulate() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();

    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .status(OrderStatus.READY)
        .build();

    customerLoyaltyPointsService.addPoints(customer, order);

    Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
  }

  @Test
  public void givenValidCustomerAndOrderWithLowTotalAmount_WhenAddingPoints_ShouldNotAccumulate() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Product product = ProductTestDataBuilder.aProductAltRamMemory().build();

    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .withItems(false)
        .status(OrderStatus.DRAFT)
        .build();
    order.addItem(product, new Quantity(1));
    order.place();
    order.markAsPaid();
    order.markAsReady();

    customerLoyaltyPointsService.addPoints(customer, order);

    Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
  }

  @Test
  public void givenNullCustomer_WhenAddingPoints_ShouldThrowNullPointerException() {
    Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

    Assertions.assertThatThrownBy(() -> customerLoyaltyPointsService.addPoints(null, order))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  public void givenNullOrder_WhenAddingPoints_ShouldThrowNullPointerException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();

    Assertions.assertThatThrownBy(() -> customerLoyaltyPointsService.addPoints(customer, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  public void givenOrderNotBelongingToCustomer_WhenAddingPoints_ShouldThrowOrderNotBelongsToCustomerException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

    Assertions.assertThatThrownBy(() -> customerLoyaltyPointsService.addPoints(customer, order))
        .isInstanceOf(OrderNotBelongsToCustomerException.class);
  }

  @Test
  public void givenOrderNotReady_WhenAddingPoints_ShouldThrowCantAddLoyaltyPointsOrderIsNotReady() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .status(OrderStatus.DRAFT)
        .build();

    Assertions.assertThatThrownBy(() -> customerLoyaltyPointsService.addPoints(customer, order))
        .isInstanceOf(CantAddLoyaltyPointsOrderIsNotReady.class);
  }
}
