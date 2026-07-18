package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class CustomerPersistenceEntityRepositoryIT {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;
    private final EntityManager entityManager;

    @Autowired
    public CustomerPersistenceEntityRepositoryIT(
            CustomerPersistenceEntityRepository customerPersistenceEntityRepository,
            EntityManager entityManager) {
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void shouldPersist() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        UUID customerId = entity.getId();

        customerPersistenceEntityRepository.saveAndFlush(entity);

        CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(customerId).orElseThrow();

        assertThat(savedEntity.getId()).isEqualTo(customerId);
        assertThat(savedEntity.getCreatedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedAt()).isNotNull();
    }

    @Test
    public void shouldCount() {
        long customersCount = customerPersistenceEntityRepository.count();
        assertThat(customersCount).isZero();
    }

    @Test
    public void shouldPopulateAuditingFieldsWhenPersisting() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        UUID customerId = entity.getId();

        customerPersistenceEntityRepository.saveAndFlush(entity);

        CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(customerId).orElseThrow();

        assertThat(savedEntity.getCreatedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedAt()).isNotNull();
    }

    @Test
    public void shouldUpdateLastModifiedFieldsWhenUpdatingEntity() throws Exception {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        UUID customerId = entity.getId();

        customerPersistenceEntityRepository.saveAndFlush(entity);

        CustomerPersistenceEntity persistedEntity = customerPersistenceEntityRepository.findById(customerId)
                .orElseThrow();
        OffsetDateTime firstLastModifiedAt = persistedEntity.getLastModifiedAt();
        var createdByUserId = persistedEntity.getCreatedByUserId();

        Thread.sleep(5);

        persistedEntity.setArchived(true);
        customerPersistenceEntityRepository.saveAndFlush(persistedEntity);

        CustomerPersistenceEntity updatedEntity = customerPersistenceEntityRepository.findById(customerId)
                .orElseThrow();

        assertThat(updatedEntity.getCreatedByUserId()).isEqualTo(createdByUserId);
        assertThat(updatedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(updatedEntity.getLastModifiedAt()).isNotNull();
        assertThat(updatedEntity.getLastModifiedAt()).isAfterOrEqualTo(firstLastModifiedAt);
        assertThat(updatedEntity.getArchived()).isTrue();
    }

    @Test
    public void shouldPreventStaleUpdates() {
        CustomerPersistenceEntity entity1 = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        UUID customerId = entity1.getId();
        customerPersistenceEntityRepository.saveAndFlush(entity1);

        // Now simulate two concurrent users get the same entity with same version
        CustomerPersistenceEntity user1Entity = customerPersistenceEntityRepository.findById(customerId).orElseThrow();
        CustomerPersistenceEntity user2Entity = customerPersistenceEntityRepository.findById(customerId).orElseThrow();

        // Detach user2Entity so it's not in the persistence context
        entityManager.detach(user2Entity);

        // user1 updates and saves first
        user1Entity.setEmail("newemail@email.com");
        customerPersistenceEntityRepository.saveAndFlush(user1Entity);

        // user2 tries to save with stale version
        user2Entity.setEmail("anotheremail@email.com");
        assertThatThrownBy(() -> customerPersistenceEntityRepository.saveAndFlush(user2Entity))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

}
