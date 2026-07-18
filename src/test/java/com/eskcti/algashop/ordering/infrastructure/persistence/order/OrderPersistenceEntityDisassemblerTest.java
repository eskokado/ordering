
package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;

class OrderPersistenceEntityDisassemblerTest {

  private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

  @Test
  void shouldDisassembleOrderPersistenceEntityToDomain() {
    OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .status(OrderStatus.PAID.name())
        .paymentMethod(PaymentMethod.CREDIT_CARD.name())
        .build();

    Order order = disassembler.toDomainEntity(entity);

    assertThat(order.id().value().toLong()).isEqualTo(entity.getId());
    assertThat(order.customerId().value()).isEqualTo(entity.getCustomerId());
    assertThat(order.totalAmount()).isEqualTo(new Money(entity.getTotalAmount()));
    assertThat(order.totalItems()).isEqualTo(new Quantity(entity.getTotalItems()));
    assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    assertThat(order.placedAt()).isEqualTo(entity.getPlacedAt());
    assertThat(order.paidAt()).isEqualTo(entity.getPaidAt());
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isEqualTo(entity.getReadyAt());
    assertThat(order.billing()).isNotNull();
    assertThat(order.billing().email().value()).isEqualTo(entity.getBilling().getEmail());
    assertThat(order.shipping()).isNotNull();
    assertThat(order.shipping().recipient().document().value())
        .isEqualTo(entity.getShipping().getRecipient().getDocument());
    assertThat(order.items()).isNotEmpty();
  }

  @Test
  void shouldDisassembleOrderPersistenceEntityWithNullFieldsToDomain() {
    OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrderWithNullFields()
        .build();

    Order order = disassembler.toDomainEntity(entity);

    assertThat(order.id().value().toLong()).isEqualTo(entity.getId());
    assertThat(order.customerId().value()).isEqualTo(entity.getCustomerId());
    assertThat(order.totalAmount()).isEqualTo(Money.ZERO);
    assertThat(order.totalItems()).isEqualTo(Quantity.ZERO);
    assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
    assertThat(order.paymentMethod()).isNull();
    assertThat(order.placedAt()).isNull();
    assertThat(order.paidAt()).isNull();
    assertThat(order.canceledAt()).isNull();
    assertThat(order.readyAt()).isNull();
    assertThat(order.billing()).isNull();
    assertThat(order.shipping()).isNull();
    assertThat(order.items()).isEmpty();
  }

  @Test
  void shouldDisassembleWithNullItems() throws Exception {
    OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
    var itemsField = OrderPersistenceEntity.class.getDeclaredField("items");
    itemsField.setAccessible(true);
    itemsField.set(entity, null);

    Order order = disassembler.toDomainEntity(entity);
    assertThat(order.items()).isEmpty();
  }
}
