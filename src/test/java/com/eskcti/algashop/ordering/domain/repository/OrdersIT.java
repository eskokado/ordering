package com.eskcti.algashop.ordering.domain.repository;

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
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
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

}
