package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;

class CustomerPersistenceEntityAssemblerTest {
    private final CustomerPersistenceEntityAssembler assembler = new CustomerPersistenceEntityAssembler();

    @Test
    void shouldAssembleCustomerPersistenceEntityFromDomain() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerPersistenceEntity entity = assembler.fromDomain(customer);

        assertThat(entity.getId()).isEqualTo(customer.id().value());
        assertThat(entity.getFirstName()).isEqualTo(customer.fullName().firstName());
        assertThat(entity.getLastName()).isEqualTo(customer.fullName().lastName());
        assertThat(entity.getBirthDate()).isEqualTo(customer.birthDate() != null ? customer.birthDate().value() : null);
        assertThat(entity.getEmail()).isEqualTo(customer.email().value());
        assertThat(entity.getPhone()).isEqualTo(customer.phone().value());
        assertThat(entity.getDocument()).isEqualTo(customer.document().value());
        assertThat(entity.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed());
        assertThat(entity.getArchived()).isEqualTo(customer.isArchived());
        assertThat(entity.getRegisteredAt()).isEqualTo(customer.registeredAt());
        assertThat(entity.getArchivedAt()).isEqualTo(customer.archivedAt());
        assertThat(entity.getLoyaltyPoints()).isEqualTo(customer.loyaltyPoints().value());
        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getAddress().getStreet()).isEqualTo(customer.address().street());
        assertThat(entity.getVersion()).isEqualTo(customer.version());
    }

    @Test
    void shouldAssembleCustomerPersistenceEntityFromDomainWithNullBirthDate() {
        Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();
        CustomerPersistenceEntity entity = assembler.fromDomain(customer);

        assertThat(entity.getBirthDate()).isNull();
    }

    @Test
    void shouldMergeCustomerPersistenceEntityWithDomain() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerPersistenceEntity existingEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        CustomerPersistenceEntity mergedEntity = assembler.merge(existingEntity, customer);

        assertThat(mergedEntity).isSameAs(existingEntity);
        assertThat(mergedEntity.getId()).isEqualTo(customer.id().value());
        assertThat(mergedEntity.getFirstName()).isEqualTo(customer.fullName().firstName());
        assertThat(mergedEntity.getLastName()).isEqualTo(customer.fullName().lastName());
        assertThat(mergedEntity.getEmail()).isEqualTo(customer.email().value());
    }
}
