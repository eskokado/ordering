package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;

import com.eskcti.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;

@Import(TestcontainerPostgreSQLConfig.class)
class ShoppingCartPersistenceEntityRepositoryIT extends AbstractPersistenceIT {

  @Autowired
  private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

  @Autowired
  private CustomerPersistenceEntityRepository customerRepository;

  @Test
  void givenCustomer_whenFindByCustomerId_thenReturnShoppingCart() {
    CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();
    customerRepository.saveAndFlush(customer);

    ShoppingCartPersistenceEntity shoppingCart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart()
        .customer(customer)
        .build();

    shoppingCartRepository.saveAndFlush(shoppingCart);

    Optional<ShoppingCartPersistenceEntity> found = shoppingCartRepository.findByCustomer_Id(customer.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(shoppingCart.getId());
  }

  @Test
  void givenNonExistentCustomer_whenFindByCustomerId_thenReturnEmpty() {
    Optional<ShoppingCartPersistenceEntity> found = shoppingCartRepository
        .findByCustomer_Id(java.util.UUID.randomUUID());

    assertThat(found).isEmpty();
  }
}
