package com.eskcti.algashop.ordering.domain.model.customer;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.assertj.core.api.Assertions;

import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({ CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class })
class CustomersIT {

  private Customers customers;

  @Autowired
  public CustomersIT(Customers customers) {
    this.customers = customers;
  }

  @Test
  public void shouldPersistAndFind() {
    Customer originalCustomer = CustomerTestDataBuilder.brandNewCustomer().build();
    CustomerId customerId = originalCustomer.id();
    customers.add(originalCustomer);

    Optional<Customer> possibleCustomer = customers.ofId(customerId);

    assertThat(possibleCustomer).isPresent();

    Customer savedCustomer = possibleCustomer.get();

    assertThat(savedCustomer).satisfies(
        s -> assertThat(s.id()).isEqualTo(customerId));
  }

  @Test
  public void shouldUpdateExistingCustomer() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    customer = customers.ofId(customer.id()).orElseThrow();
    customer.archive();

    customers.add(customer);

    Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

    Assertions.assertThat(savedCustomer.archivedAt()).isNotNull();
    Assertions.assertThat(savedCustomer.isArchived()).isTrue();

  }

  @Test
  public void shouldNotAllowStaleUpdates() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    Customer customerT1 = customers.ofId(customer.id()).orElseThrow();
    Customer customerT2 = customers.ofId(customer.id()).orElseThrow();

    customerT1.archive();
    customers.add(customerT1);

    customerT2.changeName(new FullName("Alex", "Silva"));

    Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
        .isThrownBy(() -> customers.add(customerT2));

    Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

    Assertions.assertThat(savedCustomer.archivedAt()).isNotNull();
    Assertions.assertThat(savedCustomer.isArchived()).isTrue();

  }

  @Test
  public void shouldCountExistingOrders() {
    Assertions.assertThat(customers.count()).isZero();

    Customer customer1 = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer1);

    Customer customer2 = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer2);

    Assertions.assertThat(customers.count()).isEqualTo(2L);
  }

  @Test
  public void shouldReturnValidateIfOrderExists() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    Assertions.assertThat(customers.exists(customer.id())).isTrue();
    Assertions.assertThat(customers.exists(new CustomerId())).isFalse();
  }

  @Test
  public void shouldFindCustomerByEmail() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    Optional<Customer> found = customers.ofEmail(customer.email());

    Assertions.assertThat(found).isPresent();
    Assertions.assertThat(found.get().id()).isEqualTo(customer.id());
  }

  @Test
  public void shouldNotFindCustomerByNonExistentEmail() {
    Optional<Customer> found = customers.ofEmail(new Email("nonexistent@example.com"));

    Assertions.assertThat(found).isEmpty();
  }

  @Test
  public void shouldReturnTrueWhenEmailIsUnique() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    boolean isUnique = customers.isEmailUnique(new Email("another@example.com"), customer.id());

    Assertions.assertThat(isUnique).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEmailExistsAndNotSameCustomer() {
    Customer customer1 = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer1);

    Customer customer2 = CustomerTestDataBuilder.brandNewCustomer().email(new Email("another@example.com")).build();
    customers.add(customer2);

    boolean isUnique = customers.isEmailUnique(customer2.email(), customer1.id());

    Assertions.assertThat(isUnique).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenEmailExistsButSameCustomer() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    customers.add(customer);

    boolean isUnique = customers.isEmailUnique(customer.email(), customer.id());

    Assertions.assertThat(isUnique).isTrue();
  }

}
