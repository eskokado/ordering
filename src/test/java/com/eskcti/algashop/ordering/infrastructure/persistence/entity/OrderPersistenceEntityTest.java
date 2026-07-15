package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityTest {

  @Test
  void given_validParams_whenBuild_shouldCreateOrderPersistenceEntity() {
    final var id = 1L;
    final var customerId = UUID.randomUUID();
    final var totalAmount = new BigDecimal("100.00");
    final var totalItems = 5;
    final var status = "PLACED";
    final var paymentMethod = "CREDIT_CARD";
    final var placedAt = OffsetDateTime.now();
    final var paidAt = OffsetDateTime.now().plusHours(1);
    final var canceledAt = null;
    final var readyAt = OffsetDateTime.now().plusHours(2);

    final var entity = OrderPersistenceEntity.builder()
        .id(id)
        .customerId(customerId)
        .totalAmount(totalAmount)
        .totalItems(totalItems)
        .status(status)
        .paymentMethod(paymentMethod)
        .placedAt(placedAt)
        .paidAt(paidAt)
        .canceledAt(canceledAt)
        .readyAt(readyAt)
        .build();

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getCustomerId()).isEqualTo(customerId);
    assertThat(entity.getTotalAmount()).isEqualTo(totalAmount);
    assertThat(entity.getTotalItems()).isEqualTo(totalItems);
    assertThat(entity.getStatus()).isEqualTo(status);
    assertThat(entity.getPaymentMethod()).isEqualTo(paymentMethod);
    assertThat(entity.getPlacedAt()).isEqualTo(placedAt);
    assertThat(entity.getPaidAt()).isEqualTo(paidAt);
    assertThat(entity.getCanceledAt()).isNull();
    assertThat(entity.getReadyAt()).isEqualTo(readyAt);
  }

  @Test
  void given_noArgs_whenNewInstance_shouldCreateOrderPersistenceEntity() {
    final var entity = new OrderPersistenceEntity();
    assertThat(entity).isNotNull();
  }

  @Test
  void given_twoEntitiesWithSameId_whenEquals_shouldReturnTrue() {
    final var id = 42L;

    final var entity1 = OrderPersistenceEntity.builder()
        .id(id)
        .customerId(UUID.randomUUID())
        .build();

    final var entity2 = OrderPersistenceEntity.builder()
        .id(id)
        .customerId(UUID.randomUUID())
        .build();

    assertThat(entity1).isEqualTo(entity2);
    assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
  }
}
