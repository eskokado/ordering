package com.eskcti.algashop.ordering.application.order.query;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Orders;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;

@SpringBootTest
@Transactional
class OrderQueryServiceIT {

  @Autowired
  private OrderQueryService queryService;

  @Autowired
  private Orders orders;

  @Autowired
  private Customers customers;

  @Test
  public void shouldFindById() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    Order order = OrderTestDataBuilder.aPlacedOrder()
        .customerId(customer.id())
        .build();
    orders.add(order);

    OrderDetailOutput output = queryService.findById(order.id().toString());

    Assertions.assertThat(output)
        .extracting(
            OrderDetailOutput::getId,
            OrderDetailOutput::getTotalAmount,
            OrderDetailOutput::getTotalItems,
            OrderDetailOutput::getStatus,
            OrderDetailOutput::getPaymentMethod)
        .containsExactly(
            order.id().toString(),
            order.totalAmount().value(),
            order.totalItems().value(),
            OrderStatus.PLACED.name(),
            PaymentMethod.CREDIT_CARD.name());

    Assertions.assertThat(output.getPlacedAt()).isNotNull();
    Assertions.assertThat(output.getPaidAt()).isNull();
    Assertions.assertThat(output.getCanceledAt()).isNull();
    Assertions.assertThat(output.getReadyAt()).isNull();

    Assertions.assertThat(output.getCustomer())
        .extracting(
            CustomerMinimalOutput::getId,
            CustomerMinimalOutput::getFirstName,
            CustomerMinimalOutput::getLastName,
            CustomerMinimalOutput::getEmail,
            CustomerMinimalOutput::getDocument,
            CustomerMinimalOutput::getPhone)
        .containsExactly(
            customer.id().value(),
            "John",
            "Doe",
            "johndoe@email.com",
            "255-08-0578",
            "478-256-2604");

    Assertions.assertThat(output.getItems()).hasSize(2);
    Assertions.assertThat(output.getItems())
        .extracting(
            OrderItemDetailOutput::getOrderId,
            OrderItemDetailOutput::getProductName,
            OrderItemDetailOutput::getQuantity,
            OrderItemDetailOutput::getPrice)
        .containsExactlyInAnyOrder(
            Assertions.tuple(order.id().toString(), "Notebook X11", 2, new BigDecimal("3000.00")),
            Assertions.tuple(order.id().toString(), "4GB RAM", 1, new BigDecimal("200.00")));

    Assertions.assertThat(output.getShipping())
        .extracting(
            ShippingData::getCost,
            ShippingData::getExpectedDate)
        .containsExactly(
            new BigDecimal("10.00"),
            LocalDate.now().plusWeeks(1));

    Assertions.assertThat(output.getShipping().getRecipient())
        .extracting(
            recipient -> recipient.getFirstName(),
            recipient -> recipient.getLastName(),
            recipient -> recipient.getDocument(),
            recipient -> recipient.getPhone())
        .containsExactly("John", "Doe", "112-33-2321", "111-441-1244");

    Assertions.assertThat(output.getShipping().getAddress())
        .extracting(
            address -> address.getStreet(),
            address -> address.getNumber(),
            address -> address.getComplement(),
            address -> address.getNeighborhood(),
            address -> address.getCity(),
            address -> address.getState(),
            address -> address.getZipCode())
        .containsExactly(
            "Bourbon Street",
            "1234",
            "apt. 11",
            "North Ville",
            "Montfort",
            "South Carolina",
            "79911");

    Assertions.assertThat(output.getBilling())
        .extracting(
            billing -> billing.getFirstName(),
            billing -> billing.getLastName(),
            billing -> billing.getDocument(),
            billing -> billing.getEmail(),
            billing -> billing.getPhone())
        .containsExactly(
            "John",
            "Doe",
            "225-09-1992",
            "john.doe@gmail.com",
            "123-111-9911");
  }

  @Test
  public void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
    String nonExistentOrderId = new OrderId().toString();

    Assertions.assertThatThrownBy(() -> queryService.findById(nonExistentOrderId))
        .isInstanceOf(OrderNotFoundException.class);
  }

}
