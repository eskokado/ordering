
package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;

class OrderPersistenceEntityDisassemblerTest {

  private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

  @Test
  void shouldDisassembleOrderPersistenceEntityToDomain() {
    var id = 123L;
    var customerId = UUID.randomUUID();
    var totalAmount = new BigDecimal("100.00");
    var totalItems = 5;
    var status = OrderStatus.PAID;
    var paymentMethod = PaymentMethod.CREDIT_CARD;
    var placedAt = OffsetDateTime.now();
    var paidAt = OffsetDateTime.now().plusMinutes(1);
    OffsetDateTime canceledAt = null;
    OffsetDateTime readyAt = null;

    OrderPersistenceEntity entity = OrderPersistenceEntity.builder()
        .id(id)
        .customerId(customerId)
        .totalAmount(totalAmount)
        .totalItems(totalItems)
        .status(status.name())
        .paymentMethod(paymentMethod.name())
        .placedAt(placedAt)
        .paidAt(paidAt)
        .canceledAt(canceledAt)
        .readyAt(readyAt)
        .build();

    Order order = disassembler.toDomainEntity(entity);

    assertThat(order.id().value().toLong()).isEqualTo(id);
    assertThat(order.customerId().value()).isEqualTo(customerId);
    assertThat(order.totalAmount()).isEqualTo(new Money(totalAmount));
    assertThat(order.totalItems()).isEqualTo(new Quantity(totalItems));
    assertThat(order.status()).isEqualTo(status);
    assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
    assertThat(order.placedAt()).isEqualTo(placedAt);
    assertThat(order.paidAt()).isEqualTo(paidAt);
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
  }

  @Test
  void shouldDisassembleOrderPersistenceEntityWithNullFieldsToDomain() {
    var id = 456L;
    var customerId = UUID.randomUUID();

    OrderPersistenceEntity entity = OrderPersistenceEntity.builder()
        .id(id)
        .customerId(customerId)
        .totalAmount(null)
        .totalItems(null)
        .status(null)
        .paymentMethod(null)
        .placedAt(null)
        .paidAt(null)
        .canceledAt(null)
        .readyAt(null)
        .build();

    Order order = disassembler.toDomainEntity(entity);

    assertThat(order.id().value().toLong()).isEqualTo(id);
    assertThat(order.customerId().value()).isEqualTo(customerId);
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
