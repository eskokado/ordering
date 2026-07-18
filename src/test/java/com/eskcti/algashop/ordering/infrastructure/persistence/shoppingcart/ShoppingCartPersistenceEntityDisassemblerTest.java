package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;

class ShoppingCartPersistenceEntityDisassemblerTest {

  private ShoppingCartPersistenceEntityDisassembler disassembler;

  @BeforeEach
  void setUp() {
    disassembler = new ShoppingCartPersistenceEntityDisassembler();
  }

  @Test
  void givenShoppingCartPersistenceEntity_whenDisassemble_shouldReturnShoppingCart() {
    ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
        .existingShoppingCart()
        .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
        .build();

    ShoppingCart shoppingCart = disassembler.toDomainEntity(persistenceEntity);

    assertThat(shoppingCart.id().value()).isEqualTo(persistenceEntity.getId());
    assertThat(shoppingCart.customerId().value()).isEqualTo(persistenceEntity.getCustomerId());
    assertThat(shoppingCart.totalAmount().value()).isEqualTo(persistenceEntity.getTotalAmount());
    assertThat(shoppingCart.totalItems().value()).isEqualTo(persistenceEntity.getTotalItems());
    assertThat(shoppingCart.createdAt()).isEqualTo(persistenceEntity.getCreatedAt());
    assertThat(shoppingCart.items()).hasSize(persistenceEntity.getItems().size());
    assertThat(shoppingCart.version()).isEqualTo(persistenceEntity.getVersion());
  }
}
