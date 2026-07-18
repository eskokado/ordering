package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityTest {

  @Test
  void given_validParams_whenBuild_shouldCreateOrderPersistenceEntity() {
    final var entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNotNull();
    assertThat(entity.getCustomerId()).isNotNull();
    assertThat(entity.getTotalAmount()).isNotNull();
    assertThat(entity.getTotalItems()).isEqualTo(3);
    assertThat(entity.getStatus()).isEqualTo("DRAFT");
    assertThat(entity.getPaymentMethod()).isEqualTo("CREDIT_CARD");
    assertThat(entity.getPlacedAt()).isNotNull();
    assertThat(entity.getPaidAt()).isNotNull();
    assertThat(entity.getCanceledAt()).isNull();
    assertThat(entity.getReadyAt()).isNotNull();
    assertThat(entity.getBilling()).isNotNull();
    assertThat(entity.getShipping()).isNotNull();
    assertThat(entity.getItems()).hasSize(2);
  }

  @Test
  void given_noArgs_whenNewInstance_shouldCreateOrderPersistenceEntity() {
    final var entity = new OrderPersistenceEntity();
    assertThat(entity).isNotNull();
  }

  @Test
  void given_twoEntitiesWithSameId_whenEquals_shouldReturnTrue() {
    final var id = 42L;

    final var entity1 = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .id(id)
        .build();

    final var entity2 = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .id(id)
        .build();

    assertThat(entity1).isEqualTo(entity2);
    assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
  }

  @Test
  void given_nullItem_whenAddItem_shouldDoNothing() {
    final var entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
    final int initialSize = entity.getItems().size();
    entity.addItem(null);
    assertThat(entity.getItems()).hasSize(initialSize);
  }

  @Test
  void given_itemsIsNull_whenAddItem_shouldInitializeItemsAndAdd() {
    final var entity = OrderPersistenceEntity.builder().id(1L)
        .customer(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build()).build();
    entity.setItems(null);
    final var item = OrderItemPersistenceEntity.builder().id(2L).build();
    entity.addItem(item);
    assertThat(entity.getItems()).isNotNull();
    assertThat(entity.getItems()).hasSize(1);
    assertThat(item.getOrder()).isEqualTo(entity);
  }

  @Test
  void given_itemsIsInitialized_whenAddItem_shouldAddItemAndSetOrder() {
    final var entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
    entity.setItems(new java.util.HashSet<>(entity.getItems())); // Make set modifiable
    final int initialSize = entity.getItems().size();
    final var item = OrderItemPersistenceEntity.builder().id(initialSize + 1L).build();
    entity.addItem(item);
    assertThat(entity.getItems()).hasSize(initialSize + 1);
    assertThat(entity.getItems()).contains(item);
    assertThat(item.getOrder()).isEqualTo(entity);
  }

  @Test
  void given_nullCustomer_whenGetCustomerId_shouldReturnNull() {
    final var entity = new OrderPersistenceEntity();

    assertThat(entity.getCustomerId()).isNull();
  }
}
