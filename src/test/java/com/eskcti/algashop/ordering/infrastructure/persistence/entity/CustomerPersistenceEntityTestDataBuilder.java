package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import static com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {

    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder aCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1991, 7, 5))
                .email("johndoe@email.com")
                .phone("478-256-2604")
                .document("255-08-0578")
                .promotionNotificationsAllowed(true)
                .loyaltyPoints(0)
                .address(AddressEmbeddable.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode("12345")
                        .complement("Apt. 114")
                        .build());
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return aCustomer();
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingAnonymizedCustomer() {
        return aCustomer()
                .firstName("Anonymized")
                .lastName("User")
                .email("anonymized@example.com")
                .document("000-00-0000")
                .birthDate(null)
                .phone("000-000-0000")
                .promotionNotificationsAllowed(false);
    }
}
