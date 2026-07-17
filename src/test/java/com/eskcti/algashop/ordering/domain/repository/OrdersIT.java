package com.eskcti.algashop.ordering.domain.repository;

import java.time.Year;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.repository.Customers;
import com.eskcti.algashop.ordering.domain.model.repository.Orders;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@Import({ OrdersPersistenceProvider.class, OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class, SpringDataAuditingConfig.class })
class OrdersIT {

  private Orders orders;
  private Customers customers;
  private OrderPersistenceEntityRepository entityRepository;
  private CustomerPersistenceEntityRepository customerEntityRepository;
  private final TransactionTemplate newTransaction;

  @Autowired
  public OrdersIT(Orders orders, Customers customers,
      OrderPersistenceEntityRepository entityRepository,
      CustomerPersistenceEntityRepository customerEntityRepository,
      PlatformTransactionManager transactionManager) {
    this.orders = orders;
    this.customers = customers;
    this.entityRepository = entityRepository;
    this.customerEntityRepository = customerEntityRepository;
    this.newTransaction = new TransactionTemplate(transactionManager);
    this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
  }

  @Test
  public void shouldPersistAndFind() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    Order originalOrder = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .build();
    OrderId orderId = originalOrder.id();
    orders.add(originalOrder);

    Optional<Order> possibleOrder = orders.ofId(orderId);

    assertThat(possibleOrder).isPresent();

    Order savedOrder = possibleOrder.get();

    assertThat(savedOrder).satisfies(
        s -> assertThat(s.id()).isEqualTo(orderId),
        s -> assertThat(s.customerId()).isEqualTo(originalOrder.customerId()),
        s -> assertThat(s.totalAmount()).isEqualTo(originalOrder.totalAmount()),
        s -> assertThat(s.totalItems()).isEqualTo(originalOrder.totalItems()),
        s -> assertThat(s.placedAt()).isEqualTo(originalOrder.placedAt()),
        s -> assertThat(s.paidAt()).isEqualTo(originalOrder.paidAt()),
        s -> assertThat(s.canceledAt()).isEqualTo(originalOrder.canceledAt()),
        s -> assertThat(s.readyAt()).isEqualTo(originalOrder.readyAt()),
        s -> assertThat(s.status()).isEqualTo(originalOrder.status()),
        s -> assertThat(s.paymentMethod()).isEqualTo(originalOrder.paymentMethod()));
  }

  @Test
  public void shouldReturnTrueWhenOrderExists() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .build();
    orders.add(order);

    assertThat(orders.exists(order.id())).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenOrderDoesNotExist() {
    OrderId nonExistentId = new OrderId();
    assertThat(orders.exists(nonExistentId)).isFalse();
  }

  @Test
  public void shouldCountOrders() {
    assertThat(orders.count()).isZero();

    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    orders.add(OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .build());
    assertThat(orders.count()).isEqualTo(1L);

    orders.add(OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .build());
    assertThat(orders.count()).isEqualTo(2L);
  }

  @Test
  public void shouldPersistAndFindDraftOrder() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    Order draftOrder = Order.draft(customer.id());
    OrderId orderId = draftOrder.id();
    orders.add(draftOrder);

    Optional<Order> possibleOrder = orders.ofId(orderId);

    assertThat(possibleOrder).isPresent();
    Order savedOrder = possibleOrder.get();

    assertThat(savedOrder.id()).isEqualTo(orderId);
    assertThat(savedOrder.paymentMethod()).isNull();
    assertThat(savedOrder.placedAt()).isNull();
    assertThat(savedOrder.paidAt()).isNull();
    assertThat(savedOrder.canceledAt()).isNull();
    assertThat(savedOrder.readyAt()).isNull();
  }

  @Test
  public void shouldMapEntityWithNullFields() {
    CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
    customerEntityRepository.saveAndFlush(customer);
    OrderPersistenceEntity entityWithNulls = OrderPersistenceEntityTestDataBuilder.existingOrderWithNullFields()
        .id(999L)
        .customer(customer)
        .build();

    entityRepository.saveAndFlush(entityWithNulls);

    Optional<Order> possibleOrder = orders.ofId(new OrderId(999L));

    assertThat(possibleOrder).isPresent();
    Order order = possibleOrder.get();

    assertThat(order.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(order.totalItems()).isEqualTo(Quantity.ZERO);
    assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
    assertThat(order.paymentMethod()).isNull();
    assertThat(order.placedAt()).isNull();
    assertThat(order.paidAt()).isNull();
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
  }

  @Test
  public void shouldNotAllowStaleUpdates() {
    // T0: insere o pedido em transação própria
    OrderId orderId = inNewTransaction(() -> {
      Customer customer = CustomerTestDataBuilder.existingCustomer().build();
      customers.add(customer);
      Order order = OrderTestDataBuilder.anOrder()
          .customerId(customer.id())
          .status(OrderStatus.PLACED).build();
      orders.add(order);
      return order.id();
    });

    Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
        .isThrownBy(() -> inNewTransaction(() -> {
          // T1: carrega o pedido em sua própria transação
          Order orderT1 = orders.ofId(orderId).orElseThrow();

          // T2: em outra transação separada, salva primeiro
          inNewTransaction(() -> {
            Order orderT2 = orders.ofId(orderId).orElseThrow();
            orderT2.markAsPaid();
            orders.add(orderT2);
          });

          // T1 tenta salvar com versão obsoleta
          orderT1.cancel();
          orders.add(orderT1);
        }));

    // Verifica que a atualização de T2 prevaleceu
    Order savedOrder = orders.ofId(orderId).orElseThrow();
    Assertions.assertThat(savedOrder.canceledAt()).isNull();
    Assertions.assertThat(savedOrder.paidAt()).isNotNull();
  }

  private <T> T inNewTransaction(Supplier<T> callback) {
    return newTransaction.execute(status -> callback.get());
  }

  private void inNewTransaction(Runnable callback) {
    newTransaction.executeWithoutResult(status -> callback.run());
  }

  private void setField(Object obj, String fieldName, Object value) {
    try {
      var field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void shouldGetPlacedOrdersByCustomerInYear() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    Order order1 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PLACED).build();
    setField(order1, "placedAt", OffsetDateTime.now().withYear(2024));
    orders.add(order1);

    Order order2 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PLACED).build();
    setField(order2, "placedAt", OffsetDateTime.now().withYear(2024));
    orders.add(order2);

    Order order3 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PLACED).build();
    setField(order3, "placedAt", OffsetDateTime.now().withYear(2023));
    orders.add(order3);

    List<Order> orders2024 = orders.placedByCustomerInYear(customer.id(), Year.of(2024));

    Assertions.assertThat(orders2024).hasSize(2);
    Assertions.assertThat(orders2024).extracting(Order::id).containsExactlyInAnyOrder(order1.id(), order2.id());
  }

  @Test
  public void shouldGetSalesQuantityByCustomerInYear() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    // Paid order in 2024
    Order order1 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PAID).build();
    setField(order1, "placedAt", OffsetDateTime.now().withYear(2024));
    orders.add(order1);

    // Paid order in 2024
    Order order2 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PAID).build();
    setField(order2, "placedAt", OffsetDateTime.now().withYear(2024));
    orders.add(order2);

    // Canceled order in 2024 (should not count)
    Order order3 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PLACED).build();
    setField(order3, "placedAt", OffsetDateTime.now().withYear(2024));
    order3.cancel();
    orders.add(order3);

    // Paid order in 2023 (should not count)
    Order order4 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PAID).build();
    setField(order4, "placedAt", OffsetDateTime.now().withYear(2023));
    orders.add(order4);

    long salesCount = orders.salesQuantityByCustomerInYear(customer.id(), Year.of(2024));

    Assertions.assertThat(salesCount).isEqualTo(2);
  }

  @Test
  public void shouldGetTotalSoldForCustomer() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    // Paid order (counts)
    Order order1 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PAID).withItems(true)
        .build();
    orders.add(order1);

    // Canceled order (doesn't count)
    Order order2 = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.PLACED).withItems(true)
        .build();
    order2.cancel();
    orders.add(order2);

    Money totalSold = orders.totalSoldForCustomer(customer.id());

    Assertions.assertThat(totalSold).isEqualTo(order1.totalAmount());
  }

}
