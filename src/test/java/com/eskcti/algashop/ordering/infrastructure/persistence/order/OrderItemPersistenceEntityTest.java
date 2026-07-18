package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;

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
