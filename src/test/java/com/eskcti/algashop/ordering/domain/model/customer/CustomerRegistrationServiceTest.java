package com.eskcti.algashop.ordering.domain.model.customer;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.customer.BirthDate;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

  @Mock
  private Customers customers;

  @InjectMocks
  private CustomerRegistrationService customerRegistrationService;

  @Test
  public void shouldRegister() {
    Mockito.when(customers.isEmailUnique(Mockito.any(Email.class), Mockito.any(CustomerId.class)))
        .thenReturn(true);

    Customer customer = customerRegistrationService.register(
        new FullName("John", "Doe"),
        new BirthDate(LocalDate.of(1991, 7, 5)),
        new Email("johndoe@email.com"),
        new Phone("478-256-2604"),
        new Document("255-08-0578"),
        true,
        Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("North Ville")
            .city("Yostfort")
            .state("South Carolina")
            .zipCode(new ZipCode("70283"))
            .complement("Apt. 901")
            .build());

    Assertions.assertThat(customer.fullName()).isEqualTo(new FullName("John", "Doe"));
    Assertions.assertThat(customer.email()).isEqualTo(new Email("johndoe@email.com"));
  }

  @Test
  public void shouldThrowExceptionWhenEmailIsNotUniqueOnRegister() {
    Mockito.when(customers.isEmailUnique(Mockito.any(Email.class), Mockito.any(CustomerId.class)))
        .thenReturn(false);

    Assertions.assertThatThrownBy(() -> customerRegistrationService.register(
        new FullName("John", "Doe"),
        new BirthDate(LocalDate.of(1991, 7, 5)),
        new Email("johndoe@email.com"),
        new Phone("478-256-2604"),
        new Document("255-08-0578"),
        true,
        Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("North Ville")
            .city("Yostfort")
            .state("South Carolina")
            .zipCode(new ZipCode("70283"))
            .complement("Apt. 901")
            .build()))
        .isInstanceOf(CustomerEmailIsInUseException.class);
  }

  @Test
  public void shouldChangeEmail() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Email newEmail = new Email("newemail@email.com");

    Mockito.when(customers.isEmailUnique(Mockito.eq(newEmail), Mockito.eq(customer.id())))
        .thenReturn(true);

    customerRegistrationService.changeEmail(customer, newEmail);

    Assertions.assertThat(customer.email()).isEqualTo(newEmail);
  }

  @Test
  public void shouldThrowExceptionWhenEmailIsNotUniqueOnChangeEmail() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    Email newEmail = new Email("newemail@email.com");

    Mockito.when(customers.isEmailUnique(Mockito.eq(newEmail), Mockito.eq(customer.id())))
        .thenReturn(false);

    Assertions.assertThatThrownBy(() -> customerRegistrationService.changeEmail(customer, newEmail))
        .isInstanceOf(CustomerEmailIsInUseException.class);
  }

}
