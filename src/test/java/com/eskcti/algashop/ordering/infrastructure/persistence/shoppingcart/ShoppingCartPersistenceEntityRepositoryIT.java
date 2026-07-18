package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;

@DataJpaTest
class ShoppingCartPersistenceEntityRepositoryIT {

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
