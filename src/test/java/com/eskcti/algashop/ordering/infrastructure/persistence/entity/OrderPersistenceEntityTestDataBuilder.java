package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

public class OrderPersistenceEntityTestDataBuilder {

  private static final OffsetDateTime DEFAULT_PLACED_AT = OffsetDateTime.parse("2026-07-16T10:15:30Z");
  private static final OffsetDateTime DEFAULT_PAID_AT = OffsetDateTime.parse("2026-07-16T11:15:30Z");
  private static final OffsetDateTime DEFAULT_READY_AT = OffsetDateTime.parse("2026-07-16T12:15:30Z");

  private OrderPersistenceEntityTestDataBuilder() {
  }

  public static OrderPersistenceEntityBuilder existingOrder() {
    return OrderPersistenceEntity.builder()
        .id(IdGenerator.generateTSID().toLong())
        .customer(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build())
        .totalItems(3)
        .totalAmount(new BigDecimal("1250.00"))
        .status("DRAFT")
        .paymentMethod("CREDIT_CARD")
        .placedAt(DEFAULT_PLACED_AT)
        .paidAt(DEFAULT_PAID_AT)
        .readyAt(DEFAULT_READY_AT)
        .billing(existingBilling())
        .shipping(existingShipping())
        .items(Set.of(
            existingItem().build(),
            existingItemAlt().build()));
  }

  public static OrderPersistenceEntityBuilder existingOrderWithNullFields() {
    return existingOrder()
        .totalAmount(null)
        .totalItems(null)
        .status(null)
        .paymentMethod(null)
        .placedAt(null)
        .paidAt(null)
        .canceledAt(null)
        .readyAt(null)
        .billing(null)
        .shipping(null)
        .items(Set.of());
  }

  public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingItem() {
    return OrderItemPersistenceEntity.builder()
        .id(IdGenerator.generateTSID().toLong())
        .price(new BigDecimal("500.00"))
        .quantity(2)
        .totalAmount(new BigDecimal("1000.00"))
        .productName("Notebook")
        .productId(IdGenerator.generateTimeBasedUUID());
  }

  public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingItemAlt() {
    return OrderItemPersistenceEntity.builder()
        .id(IdGenerator.generateTSID().toLong())
        .price(new BigDecimal("250.00"))
        .quantity(1)
        .totalAmount(new BigDecimal("250.00"))
        .productName("Mouse pad")
        .productId(IdGenerator.generateTimeBasedUUID());
  }

  public static BillingEmbeddable existingBilling() {
    return BillingEmbeddable.builder()
        .firstName("John")
        .lastName("Doe")
        .document("225-09-1992")
        .phone("123-111-9911")
        .email("john.doe@gmail.com")
        .address(existingAddress())
        .build();
  }

  public static ShippingEmbeddable existingShipping() {
    return ShippingEmbeddable.builder()
        .cost(new BigDecimal("10.00"))
        .expectedDate(LocalDate.of(2026, 7, 23))
        .address(existingAddress())
        .recipient(existingRecipient())
        .build();
  }

  public static RecipientEmbeddable existingRecipient() {
    return RecipientEmbeddable.builder()
        .firstName("John")
        .lastName("Doe")
        .document("112-33-2321")
        .phone("111-441-1244")
        .build();
  }

  public static AddressEmbeddable existingAddress() {
    return AddressEmbeddable.builder()
        .street("Bourbon Street")
        .number("1234")
        .complement("apt. 11")
        .neighborhood("North Ville")
        .city("Montfort")
        .state("South Carolina")
        .zipCode("79911")
        .build();
  }
}
