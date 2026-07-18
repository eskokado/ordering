package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.customer.BirthDate;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;

class CustomerPersistenceEntityDisassemblerTest {
    private final CustomerPersistenceEntityDisassembler disassembler = new CustomerPersistenceEntityDisassembler();

    @Test
    void shouldDisassembleCustomerPersistenceEntityToDomain() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        Customer customer = disassembler.toDomainEntity(entity);

        assertThat(customer.id().value()).isEqualTo(entity.getId());
        assertThat(customer.fullName()).isEqualTo(new FullName(entity.getFirstName(), entity.getLastName()));
        assertThat(customer.birthDate()).isEqualTo(new BirthDate(entity.getBirthDate()));
        assertThat(customer.email()).isEqualTo(new Email(entity.getEmail()));
        assertThat(customer.phone()).isEqualTo(new Phone(entity.getPhone()));
        assertThat(customer.document()).isEqualTo(new Document(entity.getDocument()));
        assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(entity.getLoyaltyPoints()));
        assertThat(customer.isPromotionNotificationsAllowed()).isEqualTo(entity.getPromotionNotificationsAllowed());
        assertThat(customer.isArchived()).isEqualTo(entity.getArchived());
        assertThat(customer.registeredAt()).isEqualTo(entity.getRegisteredAt());
        assertThat(customer.archivedAt()).isEqualTo(entity.getArchivedAt());
        assertThat(customer.address()).isNotNull();
        assertThat(customer.version()).isEqualTo(entity.getVersion());
    }

    @Test
    void shouldDisassembleCustomerPersistenceEntityWithNullBirthDate() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingAnonymizedCustomer()
                .build();

        Customer customer = disassembler.toDomainEntity(entity);

        assertThat(customer.id().value()).isEqualTo(entity.getId());
        assertThat(customer.birthDate()).isNull();
    }
}
