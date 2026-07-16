package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemPersistenceEntityTest {
  private OrderItemPersistenceEntity itemEntity;
  private OrderPersistenceEntity orderEntity;

  @BeforeEach
  void setUp() {
    orderEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
    itemEntity = OrderItemPersistenceEntity.builder().build();
  }

  @Test
  void getOrderId_WhenOrderIsNull_ShouldReturnNull() {
    itemEntity.setOrder(null);
    assertNull(itemEntity.getOrderId());
  }

  @Test
  void getOrderId_WhenOrderIsPresent_ShouldReturnOrderId() {
    itemEntity.setOrder(orderEntity);
    assertEquals(orderEntity.getId(), itemEntity.getOrderId());
  }
}
