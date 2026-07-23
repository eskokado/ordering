package com.eskcti.algashop.ordering.application.order.query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

  @Test
  public void shouldReturnEmptyPageWhenNoOrdersExist() {
    OrderFilter filter = orderFilter(15, 0);

    Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent()).isEmpty();
        Assertions.assertThat(page.getTotalElements()).isZero();
        Assertions.assertThat(page.getNumber()).isZero();
        Assertions.assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    public void shouldFilterOrders() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(order);

    OrderFilter filter = orderFilter(15, 0);
    Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent()).hasSize(1);

        OrderSummaryOutput summary = page.getContent().getFirst();
        Assertions.assertThat(summary)
                .extracting(
                        OrderSummaryOutput::getId,
                        OrderSummaryOutput::getTotalAmount,
                        OrderSummaryOutput::getTotalItems,
                        OrderSummaryOutput::getStatus,
                        OrderSummaryOutput::getPaymentMethod)
                .containsExactly(
                        order.id().toString(),
                        order.totalAmount().value(),
                        order.totalItems().value(),
                        OrderStatus.PLACED.name(),
                        PaymentMethod.CREDIT_CARD.name());

        Assertions.assertThat(summary.getPlacedAt()).isNotNull();
        Assertions.assertThat(summary.getPaidAt()).isNull();
        Assertions.assertThat(summary.getCanceledAt()).isNull();
        Assertions.assertThat(summary.getReadyAt()).isNull();

        Assertions.assertThat(summary.getCustomer())
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
    }

    @Test
    public void shouldPaginateOrders() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order firstOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        Order secondOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(firstOrder);
        orders.add(secondOrder);

        OrderFilter firstPageFilter = orderFilter(1, 0);
        Page<OrderSummaryOutput> firstPage = queryService.filter(firstPageFilter);

        Assertions.assertThat(firstPage.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(firstPage.getContent()).hasSize(1);
        Assertions.assertThat(firstPage.getTotalPages()).isEqualTo(2);

        OrderFilter secondPageFilter = orderFilter(1, 1);
        Page<OrderSummaryOutput> secondPage = queryService.filter(secondPageFilter);

        Assertions.assertThat(secondPage.getContent()).hasSize(1);
        Assertions.assertThat(secondPage.getNumber()).isEqualTo(1);
        Assertions.assertThat(firstPage.getContent().getFirst().getId())
                .isNotEqualTo(secondPage.getContent().getFirst().getId());
    }

    @Test
    public void shouldFilterOrdersByCustomerId() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Customer otherCustomer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        customers.add(otherCustomer);

        Order customerOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        Order otherCustomerOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(otherCustomer.id())
                .build();
        orders.add(customerOrder);
        orders.add(otherCustomerOrder);

        OrderFilter filter = orderFilterByCustomer(15, 0, customer.id().value());

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent()).hasSize(1);
        Assertions.assertThat(page.getContent().getFirst())
                .extracting(
                        OrderSummaryOutput::getId,
                        summary -> summary.getCustomer().getId())
                .containsExactly(customerOrder.id().toString(), customer.id().value());
    }

    @Test
    public void shouldReturnEmptyPageWhenCustomerHasNoOrders() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Customer otherCustomer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        customers.add(otherCustomer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(otherCustomer.id())
                .build();
        orders.add(order);

        OrderFilter filter = orderFilterByCustomer(15, 0, customer.id().value());

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent()).isEmpty();
        Assertions.assertThat(page.getTotalElements()).isZero();
    }

    @Test
    public void shouldFilterOrdersByStatus() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order placedOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        Order paidOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .status(OrderStatus.PAID)
                .build();
        orders.add(placedOrder);
        orders.add(paidOrder);

        OrderFilter filter = orderFilter(15, 0, "placed", null, null, null, null, null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getId()).isEqualTo(placedOrder.id().toString());
        Assertions.assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatus.PLACED.name());
    }

    @Test
    public void shouldIgnoreBlankStatusWhenFiltering() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .status(OrderStatus.PAID)
                .build());

        OrderFilter filter = orderFilter(15, 0, "   ", null, null, null, null, null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterOrdersByOrderId() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order firstOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        Order secondOrder = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(firstOrder);
        orders.add(secondOrder);

        OrderFilter filter = orderFilter(15, 0, null, firstOrder.id().toString(), null, null, null, null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getId()).isEqualTo(firstOrder.id().toString());
    }

    @Test
    public void shouldReturnEmptyPageWhenOrderIdIsInvalid() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());

        OrderFilter filter = orderFilter(15, 0, null, "invalid-order-id", null, null, null, null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent()).isEmpty();
        Assertions.assertThat(page.getTotalElements()).isZero();
    }

    @Test
    public void shouldFilterOrdersByPlacedAtRange() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(order);

        OffsetDateTime placedAt = order.placedAt();
        OrderFilter filter = orderFilter(15, 0, null, null, null,
                placedAt.minusDays(1), placedAt.plusDays(1), null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getId()).isEqualTo(order.id().toString());
    }

    @Test
    public void shouldReturnEmptyPageWhenPlacedAtIsOutsideRange() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(order);

        OffsetDateTime placedAt = order.placedAt();
        OrderFilter filter = orderFilter(15, 0, null, null, null,
                placedAt.plusDays(1), null, null, null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent()).isEmpty();
        Assertions.assertThat(page.getTotalElements()).isZero();
    }

    @Test
    public void shouldFilterOrdersByTotalAmountRange() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(order);

        BigDecimal totalAmount = order.totalAmount().value();
        OrderFilter filter = orderFilter(15, 0, null, null, null, null, null,
                totalAmount, totalAmount);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getTotalAmount()).isEqualByComparingTo(totalAmount);
    }

    @Test
    public void shouldReturnEmptyPageWhenTotalAmountIsOutsideRange() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .build();
        orders.add(order);

        OrderFilter filter = orderFilter(15, 0, null, null, null, null, null,
                order.totalAmount().value().add(BigDecimal.ONE), null);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent()).isEmpty();
        Assertions.assertThat(page.getTotalElements()).isZero();
    }

    @Test
    public void shouldSortOrdersByStatusAscending() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .status(OrderStatus.PAID)
                .build());

        OrderFilter filter = orderFilterWithSort(15, 0, OrderFilter.SortType.STATUS, Sort.Direction.ASC);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent())
                .extracting(OrderSummaryOutput::getStatus)
                .containsExactly(OrderStatus.PAID.name(), OrderStatus.PLACED.name());
    }

    @Test
    public void shouldSortOrdersByStatusDescending() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .status(OrderStatus.PAID)
                .build());

        OrderFilter filter = orderFilterWithSort(15, 0, OrderFilter.SortType.STATUS, Sort.Direction.DESC);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent())
                .extracting(OrderSummaryOutput::getStatus)
                .containsExactly(OrderStatus.PLACED.name(), OrderStatus.PAID.name());
    }

    @Test
    public void shouldSortOrdersByPaidAtDescending() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.aPlacedOrder()
                .customerId(customer.id())
                .status(OrderStatus.PAID)
                .build());

        OrderFilter filter = orderFilterWithSort(15, 0, OrderFilter.SortType.PAID_AT, Sort.Direction.DESC);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatus.PAID.name());
        Assertions.assertThat(page.getContent().get(1).getStatus()).isEqualTo(OrderStatus.PLACED.name());
    }

    @Test
    public void shouldSortOrdersByPlacedAtAscendingByDefault() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.aPlacedOrder().customerId(customer.id()).build());

        OrderFilter filter = orderFilter(15, 0);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getContent())
                .extracting(OrderSummaryOutput::getPlacedAt)
                .isSortedAccordingTo(OffsetDateTime::compareTo);
    }

    private OrderFilter orderFilter(int size, int page) {
        return orderFilter(size, page, null, null, null, null, null, null, null);
    }

    private OrderFilter orderFilterByCustomer(int size, int page, UUID customerId) {
        return orderFilter(size, page, null, null, customerId, null, null, null, null);
    }

    private OrderFilter orderFilter(int size, int page, String status, String orderId, UUID customerId,
            OffsetDateTime placedAtFrom, OffsetDateTime placedAtTo,
            BigDecimal totalAmountFrom, BigDecimal totalAmountTo) {
        OrderFilter filter = new OrderFilter(status, orderId, customerId, placedAtFrom, placedAtTo,
                totalAmountFrom, totalAmountTo);
        filter.setSize(size);
        filter.setPage(page);
        return filter;
    }

    private OrderFilter orderFilterWithSort(int size, int page, OrderFilter.SortType sortBy,
            Sort.Direction direction) {
        OrderFilter filter = orderFilter(size, page);
        filter.setSortByProperty(sortBy);
        filter.setSortDirection(direction);
        return filter;
    }

}
