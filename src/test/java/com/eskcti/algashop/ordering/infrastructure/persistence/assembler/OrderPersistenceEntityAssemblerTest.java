package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderItem;
import com.eskcti.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;

class OrderPersistenceEntityAssemblerTest {

  private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

  @Test
  void shouldAssembleOrderPersistenceEntityFromDomain() {
    Order order = OrderTestDataBuilder.anOrder().build();

    OrderPersistenceEntity entity = assembler.fromDomain(order);

    assertThat(entity.getId()).isEqualTo(order.id().value().toLong());
    assertThat(entity.getCustomerId()).isEqualTo(order.customerId().value());
    assertThat(entity.getTotalAmount()).isEqualTo(order.totalAmount().value());
    assertThat(entity.getTotalItems()).isEqualTo(order.totalItems().value());
    assertThat(entity.getStatus()).isEqualTo(order.status().name());
    assertThat(entity.getPaymentMethod()).isEqualTo(order.paymentMethod().name());
    assertThat(entity.getPlacedAt()).isEqualTo(order.placedAt());
    assertThat(entity.getPaidAt()).isEqualTo(order.paidAt());
    assertThat(entity.getCanceledAt()).isEqualTo(order.canceledAt());
    assertThat(entity.getReadyAt()).isEqualTo(order.readyAt());
    assertThat(entity.getBilling()).isNotNull();
    assertThat(entity.getBilling().getEmail()).isEqualTo(order.billing().email().value());
    assertThat(entity.getShipping()).isNotNull();
    assertThat(entity.getShipping().getRecipient().getPhone()).isEqualTo(order.shipping().recipient().phone().value());
  }

  @Test
  void shouldAssembleOrderPersistenceEntityFromDomainWithNullPaymentMethod() {
    Order order = OrderTestDataBuilder.draftOrder();

    OrderPersistenceEntity entity = assembler.fromDomain(order);

    assertThat(entity.getPaymentMethod()).isNull();
  }

  @Test
  void shouldMergeOrderPersistenceEntityWithDomain() {
    Order order = OrderTestDataBuilder.anOrder().build();
    OrderPersistenceEntity existingEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

    OrderPersistenceEntity mergedEntity = assembler.merge(existingEntity, order);

    assertThat(mergedEntity).isSameAs(existingEntity);
    assertThat(mergedEntity.getId()).isEqualTo(order.id().value().toLong());
    assertThat(mergedEntity.getCustomerId()).isEqualTo(order.customerId().value());
    assertThat(mergedEntity.getTotalAmount()).isEqualTo(order.totalAmount().value());
    assertThat(mergedEntity.getTotalItems()).isEqualTo(order.totalItems().value());
    assertThat(mergedEntity.getStatus()).isEqualTo(order.status().name());
    assertThat(mergedEntity.getPaymentMethod()).isEqualTo(order.paymentMethod().name());
    assertThat(mergedEntity.getPlacedAt()).isEqualTo(order.placedAt());
    assertThat(mergedEntity.getPaidAt()).isEqualTo(order.paidAt());
    assertThat(mergedEntity.getCanceledAt()).isEqualTo(order.canceledAt());
    assertThat(mergedEntity.getReadyAt()).isEqualTo(order.readyAt());
    assertThat(mergedEntity.getBilling()).isNotNull();
    assertThat(mergedEntity.getShipping()).isNotNull();
  }

  @Test
  void givenOrderWithNotItems_shouldRemovePersistenceEntityItems() {
    Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
    OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

    Assertions.assertThat(order.items()).isEmpty();
    Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

    assembler.merge(orderPersistenceEntity, order);

    Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();
  }

  @Test
  void givenOrderWithItems_shouldAddToPersistenceEntity() {
    Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
    OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .items(new HashSet<>()).build();

    Assertions.assertThat(order.items()).isNotEmpty();
    Assertions.assertThat(persistenceEntity.getItems()).isEmpty();

    assembler.merge(persistenceEntity, order);

    Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
    Assertions.assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
  }

  @Test
  void givenOrderWithItems_whenMerge_shouldRemoveMergeCorrectly() {
    Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

    Assertions.assertThat(order.items().size()).isEqualTo(2);

    Set<OrderItemPersistenceEntity> orderItemPersistenceEntities = order.items().stream()
        .map(i -> assembler.fromDomain(i))
        .collect(Collectors.toSet());

    OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .items(orderItemPersistenceEntities)
        .build();

    OrderItem orderItem = order.items().iterator().next();
    order.removeItem(orderItem.id());

    assembler.merge(persistenceEntity, order);

    Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
    Assertions.assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
  }

}
