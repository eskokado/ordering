package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;

@DataJpaTest
@Import({
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class CustomersPersistenceProviderIT {

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
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
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
