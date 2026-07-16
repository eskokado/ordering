package com.eskcti.algashop.ordering.domain.model.entity;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerArchivedException;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.BirthDate;
import com.eskcti.algashop.ordering.domain.model.valueobject.Document;
import com.eskcti.algashop.ordering.domain.model.valueobject.Email;
import com.eskcti.algashop.ordering.domain.model.valueobject.FullName;
import com.eskcti.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.valueobject.Phone;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;

import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

  @Test
  void given_brandNewCustomer_whenBuild_shouldInitializeDefaults() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

    assertThat(customer.id()).isNotNull();
    assertThat(customer.isArchived()).isFalse();
    assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    assertThat(customer.registeredAt()).isNotNull();
    assertThat(customer.archivedAt()).isNull();
  }

  @Test
  void given_customer_whenChangeData_shouldUpdateFields() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

    customer.changeName(new FullName("Jane", "Smith"));
    customer.changeEmail(new Email("jane.smith@gmail.com"));
    customer.changePhone(new Phone("111-222-3333"));
    customer.changeAddress(Address.builder()
        .street("New Street")
        .number("99")
        .neighborhood("Downtown")
        .city("York")
        .state("South California")
        .zipCode(new ZipCode("54321"))
        .build());
    customer.enablePromotionNotifications();
    customer.disablePromotionNotifications();

    assertThat(customer.fullName()).isEqualTo(new FullName("Jane", "Smith"));
    assertThat(customer.email()).isEqualTo(new Email("jane.smith@gmail.com"));
    assertThat(customer.phone()).isEqualTo(new Phone("111-222-3333"));
    assertThat(customer.address().street()).isEqualTo("New Street");
    assertThat(customer.isPromotionNotificationsAllowed()).isFalse();
  }

  @Test
  void given_customersWithSameId_whenCompare_shouldBeEqual() {
    CustomerId customerId = new CustomerId();
    Customer first = CustomerTestDataBuilder.existingCustomer().id(customerId).build();
    Customer second = CustomerTestDataBuilder.existingCustomer().id(customerId).build();

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void given_customersWithDifferentId_whenCompare_shouldNotBeEqual() {
    Customer first = CustomerTestDataBuilder.existingCustomer().build();
    Customer second = CustomerTestDataBuilder.existingCustomer().build();

    assertThat(first).isNotEqualTo(second);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("not-a-customer");
  }

  @Test
  void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> CustomerTestDataBuilder.brandNewCustomer()
            .email(new Email("invalid")).build());
  }

  @Test
  void given_invalidEmail_whenTryUpdatedCustomerEmail_shouldGenerateException() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> customer.changeEmail(new Email("invalid")));
  }

  @Test
  void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();

    customer.archive();

    Assertions.assertWith(customer,
        c -> assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
        c -> assertThat(c.email()).isNotEqualTo(new Email("john.doe@gmail.com")),
        c -> assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
        c -> assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
        c -> assertThat(c.birthDate()).isNull(),
        c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
        c -> assertThat(c.address()).isEqualTo(
            Address.builder()
                .street("Bourbon Street")
                .number("Anonymized")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement(null)
                .build()));

  }

  @Test
  void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
    Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(customer::archive);

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customer.changeEmail(new Email("email@gmail.com")));

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customer.changePhone(new Phone("123-123-1111")));

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customer.changeName(new FullName("John", "Doe")));

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customer.changeAddress(CustomerTestDataBuilder.brandNewCustomer().build().address()));

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(10)));

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(customer::enablePromotionNotifications);

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(customer::disablePromotionNotifications);
  }

  @Test
  void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

    customer.addLoyaltyPoints(new LoyaltyPoints(10));
    customer.addLoyaltyPoints(new LoyaltyPoints(20));

    Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
  }

  @Test
  void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
  }
}
