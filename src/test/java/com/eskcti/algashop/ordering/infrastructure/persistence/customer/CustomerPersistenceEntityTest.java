package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;

class CustomerPersistenceEntityTest {

    @Test
    void given_validParams_whenBuild_shouldCreateCustomerPersistenceEntity() {
        final var entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getBirthDate()).isNotNull();
        assertThat(entity.getEmail()).isEqualTo("johndoe@email.com");
        assertThat(entity.getPhone()).isEqualTo("478-256-2604");
        assertThat(entity.getDocument()).isEqualTo("255-08-0578");
        assertThat(entity.getPromotionNotificationsAllowed()).isTrue();
        assertThat(entity.getArchived()).isFalse();
        assertThat(entity.getRegisteredAt()).isNotNull();
        assertThat(entity.getArchivedAt()).isNull();
        assertThat(entity.getLoyaltyPoints()).isEqualTo(0);
        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getVersion()).isNull();
    }

    @Test
    void given_noArgs_whenNewInstance_shouldCreateCustomerPersistenceEntity() {
        final var entity = new CustomerPersistenceEntity();
        assertThat(entity).isNotNull();
    }

    @Test
    void given_twoEntitiesWithSameId_whenEquals_shouldReturnTrue() {
        final var id = java.util.UUID.randomUUID();
        final var entity1 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(id).build();
        final var entity2 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(id).build();
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }
}
