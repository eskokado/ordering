package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import com.eskcti.algashop.ordering.domain.model.IdGenerator;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder;

public class ShoppingCartPersistenceEntityTestDataBuilder {

  private ShoppingCartPersistenceEntityTestDataBuilder() {
  }

  public static ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
    return ShoppingCartPersistenceEntity.builder()
        .id(IdGenerator.generateTimeBasedUUID())
        .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
        .totalItems(3)
        .totalAmount(new BigDecimal("1250.00"))
        .createdAt(OffsetDateTime.now())
        .items(Set.of(
            existingItem().build(),
            existingItemAlt().build()));
  }

  public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItem() {
    return ShoppingCartItemPersistenceEntity.builder()
        .id(IdGenerator.generateTimeBasedUUID())
        .price(new BigDecimal(500))
        .quantity(2)
        .available(true)
        .totalAmount(new BigDecimal(1000))
        .name("Notebook")
        .productId(IdGenerator.generateTimeBasedUUID());
  }

  public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItemAlt() {
    return ShoppingCartItemPersistenceEntity.builder()
        .id(IdGenerator.generateTimeBasedUUID())
        .price(new BigDecimal(250))
        .quantity(1)
        .available(true)
        .totalAmount(new BigDecimal(250))
        .name("Mouse pad")
        .productId(IdGenerator.generateTimeBasedUUID());
  }
}
