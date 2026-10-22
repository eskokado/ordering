package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.utils.SecurityCheckTestConfig;
import com.eskcti.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eskcti.algashop.ordering.core.domain.model.commons.Email;
import com.eskcti.algashop.ordering.core.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import org.springframework.context.annotation.Import;

@Import({
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class,
    TestcontainerPostgreSQLConfig.class,
    SecurityCheckTestConfig.class
})
class CustomersPersistenceProviderIT extends AbstractPersistenceIT {

  private final CustomersPersistenceProvider persistenceProvider;
  private final CustomerPersistenceEntityRepository entityRepository;

  @Autowired
  public CustomersPersistenceProviderIT(CustomersPersistenceProvider persistenceProvider,
      CustomerPersistenceEntityRepository entityRepository) {
    this.persistenceProvider = persistenceProvider;
    this.entityRepository = entityRepository;
  }

  @Test
  public void shouldUpdateAndKeepPersistenceEntityState() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    var customerId = customer.id().value();
    persistenceProvider.add(customer);

    var persistenceEntity = entityRepository.findById(customerId).orElseThrow();

    Assertions.assertThat(persistenceEntity.getEmail()).isEqualTo(customer.email().value());
    Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

    customer = persistenceProvider.ofId(customer.id()).orElseThrow();
    customer.changeEmail(new Email("updated@email.com"));
    persistenceProvider.add(customer);

    persistenceEntity = entityRepository.findById(customerId).orElseThrow();

    Assertions.assertThat(persistenceEntity.getEmail()).isEqualTo("updated@email.com");
    Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    Assertions.assertThat(customer.version()).isNotNull();
  }

  @Test
  public void shouldAddFindAndNotFailWhenNoTransaction() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    persistenceProvider.add(customer);

    Assertions.assertThatNoException().isThrownBy(
        () -> persistenceProvider.ofId(customer.id()).orElseThrow());
  }

  @Test
  public void shouldReturnTrueWhenCustomerExists() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    persistenceProvider.add(customer);

    Assertions.assertThat(persistenceProvider.exists(customer.id())).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenCustomerDoesNotExist() {
    Assertions.assertThat(persistenceProvider.exists(new CustomerId())).isFalse();
  }

  @Test
  public void shouldCountCustomers() {
    Assertions.assertThat(persistenceProvider.count()).isZero();

    persistenceProvider.add(CustomerTestDataBuilder.brandNewCustomer()
        .fullName(new FullName("John", "Doe"))
        .email(new Email("johndoe@email.com"))
        .build());
    Assertions.assertThat(persistenceProvider.count()).isEqualTo(1L);

    persistenceProvider.add(CustomerTestDataBuilder.existingCustomer()
        .fullName(new FullName("Jane", "Doe"))
        .email(new Email("janedoe@email.com"))
        .build());
    Assertions.assertThat(persistenceProvider.count()).isEqualTo(2L);
  }

}
