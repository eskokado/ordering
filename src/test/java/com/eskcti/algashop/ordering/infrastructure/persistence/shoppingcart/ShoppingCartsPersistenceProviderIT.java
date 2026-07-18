package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;

@DataJpaTest
@Import({ CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class, ShoppingCartsPersistenceProvider.class,
    ShoppingCartPersistenceEntityAssembler.class, ShoppingCartPersistenceEntityDisassembler.class })
class ShoppingCartsPersistenceProviderIT {

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
  void givenShoppingCart_whenAdd_shouldPersistShoppingCart() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());

    shoppingCarts.add(shoppingCart);

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());
    assertThat(found).isPresent();
  }

  @Test
  void givenShoppingCart_whenAddAndUpdate_shouldIncreaseVersion() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);
    Long initialVersion = shoppingCart.version();

    setField(shoppingCart, "totalItems", new com.eskcti.algashop.ordering.domain.model.commons.Quantity(5));
    shoppingCarts.add(shoppingCart);
    Long versionAfterUpdate = shoppingCart.version();

    assertThat(versionAfterUpdate).isGreaterThan(initialVersion);
  }

  @Test
  void givenShoppingCart_whenConcurrentUpdate_shouldThrowOptimisticLockingFailureException() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    ShoppingCart cartFromAnotherTransaction = inNewTransaction(
        () -> shoppingCarts.ofId(shoppingCart.id()).orElseThrow());

    setField(cartFromAnotherTransaction, "totalItems",
        new com.eskcti.algashop.ordering.domain.model.commons.Quantity(5));

    inNewTransaction(() -> shoppingCarts.add(shoppingCart));

    org.assertj.core.api.Assertions
        .assertThatThrownBy(() -> inNewTransaction(() -> shoppingCarts.add(cartFromAnotherTransaction)))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }

  @Test
  void givenShoppingCart_whenRemove_shouldDeleteShoppingCart() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    shoppingCarts.remove(shoppingCart);

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());
    assertThat(found).isEmpty();
  }

  @Test
  void givenShoppingCart_whenRemoveById_shouldDeleteShoppingCart() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);

    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    shoppingCarts.remove(shoppingCart.id());

    Optional<ShoppingCart> found = shoppingCarts.ofId(shoppingCart.id());
    assertThat(found).isEmpty();
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
