package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderItem;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

  @Mock
  private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

  @InjectMocks
  private OrderPersistenceEntityAssembler assembler;

  @BeforeEach
  public void setup() {
    Mockito.lenient().when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
        .then(a -> {
          UUID customerId = a.getArgument(0, UUID.class);
          return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
        });
  }

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

  @Test
  void givenExistingEntityWithNullItems_shouldHandleCorrectly() throws Exception {
    Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
    OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
    var itemsField = OrderPersistenceEntity.class.getDeclaredField("items");
    itemsField.setAccessible(true);
    itemsField.set(persistenceEntity, null);

    assembler.merge(persistenceEntity, order);
    assertThat(persistenceEntity.getItems()).isNotEmpty();
  }

  @Test
  void givenNullAddress_whenInvokeToAddressEmbeddable_shouldReturnNull() throws Exception {
    var method = OrderPersistenceEntityAssembler.class
        .getDeclaredMethod("toAddressEmbeddable", com.eskcti.algashop.ordering.domain.model.commons.Address.class);
    method.setAccessible(true);

    Object result = method.invoke(assembler, new Object[] { null });

    assertThat(result).isNull();
  }

  @Test
  void givenNullRecipient_whenInvokeToRecipientEmbeddable_shouldReturnNull() throws Exception {
    var method = OrderPersistenceEntityAssembler.class
        .getDeclaredMethod("toRecipientEmbeddable",
            com.eskcti.algashop.ordering.domain.model.order.Recipient.class);
    method.setAccessible(true);

    Object result = method.invoke(assembler, new Object[] { null });

    assertThat(result).isNull();
  }

  @Test
  void givenOrderWithExtraItem_whenMerge_shouldCreateMissingPersistenceItem() {
    Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

    OrderItemPersistenceEntity existingItem = assembler.fromDomain(order.items().iterator().next());
    OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
        .items(new HashSet<>(Set.of(existingItem)))
        .build();

    assembler.merge(persistenceEntity, order);

    Assertions.assertThat(persistenceEntity.getItems()).hasSize(order.items().size());
    Assertions.assertThat(persistenceEntity.getItems())
        .extracting(OrderItemPersistenceEntity::getId)
        .containsExactlyInAnyOrderElementsOf(order.items().stream()
            .map(item -> item.id().value().toLong())
            .collect(Collectors.toSet()));
  }

  @Test
  void givenOrderMockWithNullItems_whenMerge_shouldRemovePersistenceEntityItems() {
    Order order = Mockito.mock(Order.class);
    UUID customerId = UUID.randomUUID();

    Mockito.when(order.id()).thenReturn(OrderTestDataBuilder.anOrder().build().id());
    Mockito.when(order.customerId())
        .thenReturn(new com.eskcti.algashop.ordering.domain.model.customer.CustomerId(customerId));
    Mockito.when(order.totalAmount()).thenReturn(new com.eskcti.algashop.ordering.domain.model.commons.Money("0"));
    Mockito.when(order.totalItems()).thenReturn(com.eskcti.algashop.ordering.domain.model.commons.Quantity.ZERO);
    Mockito.when(order.status()).thenReturn(com.eskcti.algashop.ordering.domain.model.order.OrderStatus.DRAFT);
    Mockito.when(order.paymentMethod()).thenReturn(null);
    Mockito.when(order.placedAt()).thenReturn(null);
    Mockito.when(order.paidAt()).thenReturn(null);
    Mockito.when(order.canceledAt()).thenReturn(null);
    Mockito.when(order.readyAt()).thenReturn(null);
    Mockito.when(order.version()).thenReturn(null);
    Mockito.when(order.billing()).thenReturn(null);
    Mockito.when(order.shipping()).thenReturn(null);
    Mockito.when(order.items()).thenReturn(null);

    OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

    assembler.merge(persistenceEntity, order);

    Assertions.assertThat(persistenceEntity.getItems()).isEmpty();
    Mockito.verify(customerPersistenceEntityRepository).getReferenceById(customerId);
  }

}
