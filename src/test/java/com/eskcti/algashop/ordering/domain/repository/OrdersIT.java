package com.eskcti.algashop.ordering.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.repository.Orders;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ OrdersPersistenceProvider.class, OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class })
class OrdersIT {

  private Orders orders;
  private OrderPersistenceEntityRepository entityRepository;

  @Autowired
  public OrdersIT(Orders orders, OrderPersistenceEntityRepository entityRepository) {
    this.orders = orders;
    this.entityRepository = entityRepository;
  }

  @Test
  public void shouldPersistAndFind() {
    Order originalOrder = OrderTestDataBuilder.anOrder().build();
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
    Order order = OrderTestDataBuilder.anOrder().build();
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

    orders.add(OrderTestDataBuilder.anOrder().build());
    assertThat(orders.count()).isEqualTo(1);

    orders.add(OrderTestDataBuilder.anOrder().build());
    assertThat(orders.count()).isEqualTo(2);
  }

  @Test
  public void shouldPersistAndFindDraftOrder() {
    Order draftOrder = Order.draft(new CustomerId());
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
    OrderPersistenceEntity entityWithNulls = OrderPersistenceEntity.builder()
        .id(999L)
        .customerId(UUID.randomUUID())
        .totalAmount(null)
        .totalItems(null)
        .status(null)
        .paymentMethod(null)
        .placedAt(null)
        .paidAt(null)
        .canceledAt(null)
        .readyAt(null)
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
}