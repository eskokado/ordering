package com.eskcti.algashop.ordering.domain.model.customer;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    CustomerRegistrationService.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})

@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class CustomerRegistrationServiceIT {

  @Autowired
  private CustomerRegistrationService customerRegistrationService;

  @Autowired
  private Customers customers;

  @Autowired
  private CustomerPersistenceEntityRepository customerRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @BeforeEach
  void beforeEach() {
    inNewTransaction(() -> customerRepository.deleteAll());
  }

  @AfterEach
  void afterEach() {
    inNewTransaction(() -> customerRepository.deleteAll());
  }

  private void inNewTransaction(Runnable callback) {
    transactionTemplate.executeWithoutResult(status -> callback.run());
  }

  @Test
  public void shouldRegister() {
    Customer customer = customerRegistrationService.register(
        new FullName("John", "Doe"),
        new BirthDate(LocalDate.of(1991, 7, 5)),
        new Email("johndoe@email.com"),
        new Phone("478-256-2604"),
        new Document("255-08-0578"),
        true,
        sampleAddress());

    Assertions.assertThat(customer.fullName()).isEqualTo(new FullName("John", "Doe"));
    Assertions.assertThat(customer.email()).isEqualTo(new Email("johndoe@email.com"));
  }

  @Test
  public void shouldThrowExceptionWhenEmailIsNotUniqueOnRegister() {
    Email email = new Email("johndoe@email.com");
    customers.add(CustomerTestDataBuilder.existingCustomer().email(email).build());

    Assertions.assertThatThrownBy(() -> customerRegistrationService.register(
        new FullName("John", "Doe"),
        new BirthDate(LocalDate.of(1991, 7, 5)),
        email,
        new Phone("478-256-2604"),
        new Document("255-08-0578"),
        true,
        sampleAddress()))
        .isInstanceOf(CustomerEmailIsInUseException.class);
  }

  @Test
  public void shouldChangeEmail() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Email newEmail = new Email("newemail@email.com");

    customerRegistrationService.changeEmail(customer, newEmail);

    Assertions.assertThat(customer.email()).isEqualTo(newEmail);
  }

  @Test
  public void shouldThrowExceptionWhenEmailIsNotUniqueOnChangeEmail() {
    Email takenEmail = new Email("taken@email.com");
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(CustomerTestDataBuilder.existingCustomer()
        .id(CustomerTestDataBuilder.SECOND_CUSTOMER_ID)
        .email(takenEmail)
        .build());

    Assertions.assertThatThrownBy(() -> customerRegistrationService.changeEmail(customer, takenEmail))
        .isInstanceOf(CustomerEmailIsInUseException.class);
  }

  private Address sampleAddress() {
    return Address.builder()
        .street("Bourbon Street")
        .number("1134")
        .neighborhood("North Ville")
        .city("Yostfort")
        .state("South Carolina")
        .zipCode(new ZipCode("70283"))
        .complement("Apt. 901")
        .build();
  }

}
