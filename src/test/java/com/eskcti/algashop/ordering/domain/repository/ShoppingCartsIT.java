package com.eskcti.algashop.ordering.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.eskcti.algashop.ordering.domain.model.repository.Customers;
import com.eskcti.algashop.ordering.domain.model.repository.ShoppingCarts;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.ShoppingCartsPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;

@DataJpaTest
@Import({ CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class, ShoppingCartsPersistenceProvider.class,
    ShoppingCartPersistenceEntityAssembler.class, ShoppingCartPersistenceEntityDisassembler.class })
class ShoppingCartsIT {

  @Autowired
  private ShoppingCarts shoppingCarts;

  @Autowired
  private Customers customers;

  @Autowired
  private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

  @Autowired
  private CustomerPersistenceEntityRepository customerRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @BeforeEach
  void beforeEach() {
    inNewTransaction(() -> {
      shoppingCartRepository.deleteAll();
      customerRepository.deleteAll();
    });
  }

  @AfterEach
  void afterEach() {
    inNewTransaction(() -> {
      shoppingCartRepository.deleteAll();
      customerRepository.deleteAll();
    });
  }

  private <T> T inNewTransaction(Supplier<T> callback) {
    return transactionTemplate.execute(status -> callback.get());
  }

  private void inNewTransaction(Runnable callback) {
    transactionTemplate.executeWithoutResult(status -> callback.run());
  }

  private void setField(Object obj, String fieldName, Object value) {
    try {
      var field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  void givenNewShoppingCart_whenAdd_shouldPersistShoppingCart() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());

    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(shoppingCart.id());
  }

  @Test
  void givenShoppingCart_whenCount_thenReturnCorrectCount() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    long count = shoppingCarts.count();
    assertThat(count).isEqualTo(1);
  }

  @Test
  void givenShoppingCart_whenRemove_thenShoppingCartShouldNotExist() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);
    shoppingCarts.remove(shoppingCart);

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());
    assertThat(found).isEmpty();
  }

  @Test
  void givenShoppingCart_whenRemoveById_thenShoppingCartShouldNotExist() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);
    shoppingCarts.remove(shoppingCart.id());

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());
    assertThat(found).isEmpty();
  }

  @Test
  void givenShoppingCart_whenUpdateWithOldVersion_thenThrowObjectOptimisticLockingFailureException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);
    ShoppingCart cartFromAnotherTransaction = inNewTransaction(
        () -> shoppingCarts.ofId(shoppingCart.id()).orElseThrow());
    setField(cartFromAnotherTransaction, "totalItems",
        new com.eskcti.algashop.ordering.domain.model.valueobject.Quantity(5));
    inNewTransaction(() -> shoppingCarts.add(shoppingCart));

    org.assertj.core.api.Assertions
        .assertThatThrownBy(() -> inNewTransaction(() -> shoppingCarts.add(cartFromAnotherTransaction)))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }

  @Test
  void givenCustomer_whenOfCustomer_thenReturnShoppingCart() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    Optional<ShoppingCart> found = shoppingCarts.ofCustomer(customer.id());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(shoppingCart.id());
  }
}
