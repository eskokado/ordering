package com.eskcti.algashop.ordering.domain.model.customer;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import java.time.LocalDate;

public class CustomerTestDataBuilder {

  public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId(
      UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a"));

  public static final CustomerId SECOND_CUSTOMER_ID = new CustomerId(
      UUID.fromString("7f259ce6-58a7-4133-b0eb-18dfbb305e8b"));

  public static final Email DEFAULT_CUSTOMER_EMAIL = new Email("johndoe@email.com");

  public static final Email SECOND_CUSTOMER_EMAIL = new Email("janedoe@email.com");

  private CustomerTestDataBuilder() {
  }

  public static Customer.BrandNewCustomerBuild brandNewCustomer() {
    return Customer.brandNew()
        .fullName(new FullName("John", "Doe"))
        .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
        .email(DEFAULT_CUSTOMER_EMAIL)
        .phone(new Phone("478-256-2604"))
        .document(new Document("255-08-0578"))
        .promotionNotificationsAllowed(true)
        .address(Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("North Ville")
            .city("York")
            .state("South California")
            .zipCode(new ZipCode("12345"))
            .complement("Apt. 114")
            .build());
  }

  public static Customer.ExistingCustomerBuild existingCustomer() {
    return Customer.existing()
        .id(DEFAULT_CUSTOMER_ID)
        .registeredAt(OffsetDateTime.now())
        .promotionNotificationsAllowed(true)
        .archived(false)
        .archivedAt(null)
        .fullName(new FullName("John", "Doe"))
        .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
        .email(DEFAULT_CUSTOMER_EMAIL)
        .phone(new Phone("478-256-2604"))
        .document(new Document("255-08-0578"))
        .promotionNotificationsAllowed(true)
        .loyaltyPoints(LoyaltyPoints.ZERO)
        .address(Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("North Ville")
            .city("York")
            .state("South California")
            .zipCode(new ZipCode("12345"))
            .complement("Apt. 114")
            .build());
  }

  public static Customer.ExistingCustomerBuild existingAnonymizedCustomer() {
    return Customer.existing()
        .id(new CustomerId())
        .fullName(new FullName("Anonymous", "Anonymous"))
        .birthDate(null)
        .email(new Email("anonymous@anonymous.com"))
        .phone(new Phone("000-000-0000"))
        .document(new Document("000-00-0000"))
        .promotionNotificationsAllowed(false)
        .archived(true)
        .registeredAt(OffsetDateTime.now())
        .archivedAt(OffsetDateTime.now())
        .loyaltyPoints(new LoyaltyPoints(10))
        .address(Address.builder()
            .street("Bourbon Street")
            .number("1134")
            .neighborhood("North Ville")
            .city("York")
            .state("South California")
            .zipCode(new ZipCode("12345"))
            .complement("Apt. 114")
            .build());
  }
}