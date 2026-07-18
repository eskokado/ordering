package com.eskcti.algashop.ordering.domain.model.service;

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

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerEmailIsInUseException;
import com.eskcti.algashop.ordering.domain.model.repository.Customers;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.BirthDate;
import com.eskcti.algashop.ordering.domain.model.valueobject.Document;
import com.eskcti.algashop.ordering.domain.model.valueobject.Email;
import com.eskcti.algashop.ordering.domain.model.valueobject.FullName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Phone;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;
import com.eskcti.algashop.ordering.infrastructure.beans.DomainServiceScanConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    DomainServiceScanConfig.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
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
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Email takenEmail = new Email("taken@email.com");
    customers.add(CustomerTestDataBuilder.existingCustomer().email(takenEmail).build());

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
