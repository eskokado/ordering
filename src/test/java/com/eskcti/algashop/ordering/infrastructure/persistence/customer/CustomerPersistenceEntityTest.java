package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;

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
        final var id = UUID.randomUUID();
        final var entity1 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(id).build();
        final var entity2 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(id).build();
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    void given_twoEntitiesWithDifferentId_whenEquals_shouldReturnFalse() {
        final var entity1 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(UUID.randomUUID()).build();
        final var entity2 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(UUID.randomUUID()).build();

        assertThat(entity1).isNotEqualTo(entity2);
        assertThat(entity1).isNotEqualTo(null);
        assertThat(entity1).isNotEqualTo("not-an-entity");
    }

    @Test
    void givenNullEvents_whenAddEvents_shouldDoNothing() {
        final var entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        entity.addEvents(null);

        assertThat(entity.getEvents()).isEmpty();
    }

    @Test
    void givenDomainEvents_whenAddEvents_shouldRegisterAndReturnEvents() {
        final var entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        final var customerId = new CustomerId(entity.getId());
        final var registeredEvent = new CustomerRegisteredEvent(customerId, entity.getRegisteredAt(),
                new FullName("John", "Doe"), new Email("johndoe@email.com"));
        final var archivedEvent = new CustomerArchivedEvent(customerId, entity.getRegisteredAt());

        entity.addEvents(List.of(registeredEvent, archivedEvent));

        assertThat(entity.getEvents()).containsExactly(registeredEvent, archivedEvent);
    }
}
