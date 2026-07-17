package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceEntityAssemblerTest {

  @Mock
  private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

  private ShoppingCartPersistenceEntityAssembler assembler;

  @BeforeEach
  void setUp() {
    assembler = new ShoppingCartPersistenceEntityAssembler(customerPersistenceEntityRepository);
  }

  @Test
  void givenShoppingCart_whenAssembleFromDomain_shouldReturnShoppingCartPersistenceEntity() {
    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart();
    UUID customerId = shoppingCart.customerId().value();

    Mockito.lenient().when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
        .thenReturn(CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build());

    ShoppingCartPersistenceEntity persistenceEntity = assembler.fromDomain(shoppingCart);

    assertThat(persistenceEntity.getId()).isEqualTo(shoppingCart.id().value());
    assertThat(persistenceEntity.getCustomer().getId()).isEqualTo(customerId);
    assertThat(persistenceEntity.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
    assertThat(persistenceEntity.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
    assertThat(persistenceEntity.getCreatedAt()).isEqualTo(shoppingCart.createdAt());
    assertThat(persistenceEntity.getItems()).hasSize(shoppingCart.items().size());
    assertThat(persistenceEntity.getVersion()).isEqualTo(shoppingCart.version());
  }

  @Test
  void givenExistingShoppingCart_whenMerge_shouldUpdateValues() {
    ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart();
    UUID customerId = shoppingCart.customerId().value();

    Mockito.lenient().when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
        .thenReturn(CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build());

    ShoppingCartPersistenceEntity persistenceEntity = new ShoppingCartPersistenceEntity();
    ShoppingCartPersistenceEntity merged = assembler.merge(persistenceEntity, shoppingCart);

    assertThat(merged.getId()).isEqualTo(shoppingCart.id().value());
    assertThat(merged.getCustomer().getId()).isEqualTo(customerId);
    assertThat(merged.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
    assertThat(merged.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
    assertThat(merged.getCreatedAt()).isEqualTo(shoppingCart.createdAt());
    assertThat(merged.getItems()).hasSize(shoppingCart.items().size());
    assertThat(merged.getVersion()).isEqualTo(shoppingCart.version());
  }
}
