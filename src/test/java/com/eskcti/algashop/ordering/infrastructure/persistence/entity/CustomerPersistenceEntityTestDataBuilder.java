package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;

public class CustomerPersistenceEntityTestDataBuilder {
    private CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1991, 7, 5))
                .email("johndoe@email.com")
                .phone("478-256-2604")
                .document("255-08-0578")
                .promotionNotificationsAllowed(true)
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(null)
                .loyaltyPoints(0)
                .address(AddressEmbeddable.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .complement("Apt. 114")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode("12345")
                        .build());
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingAnonymizedCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(UUID.randomUUID())
                .firstName("Anonymous")
                .lastName("Anonymous")
                .birthDate(null)
                .email("anonymous@anonymous.com")
                .phone("000-000-0000")
                .document("000-00-0000")
                .promotionNotificationsAllowed(false)
                .archived(true)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .loyaltyPoints(10)
                .address(AddressEmbeddable.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .complement("Apt. 114")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode("12345")
                        .build());
    }
}
