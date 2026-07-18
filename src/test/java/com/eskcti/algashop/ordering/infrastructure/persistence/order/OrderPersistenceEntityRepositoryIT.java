package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;
    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;
    private final EntityManager entityManager;

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository,
            CustomerPersistenceEntityRepository customerPersistenceEntityRepository,
            EntityManager entityManager) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void shouldPersist() {
        CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(customer);
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customer)
                .items(null)
                .build();
        long orderId = entity.getId();

        orderPersistenceEntityRepository.saveAndFlush(entity);

        OrderPersistenceEntity savedEntity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();

        assertThat(savedEntity.getId()).isEqualTo(orderId);
        assertThat(savedEntity.getCreatedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedAt()).isNotNull();
    }

    @Test
    public void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        assertThat(ordersCount).isZero();
    }

    @Test
    public void shouldPopulateAuditingFieldsWhenPersisting() {
        CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(customer);
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customer)
                .items(null)
                .build();
        long orderId = entity.getId();

        orderPersistenceEntityRepository.saveAndFlush(entity);

        OrderPersistenceEntity savedEntity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();

        assertThat(savedEntity.getCreatedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(savedEntity.getLastModifiedAt()).isNotNull();
    }

    @Test
    public void shouldUpdateLastModifiedFieldsWhenUpdatingEntity() throws Exception {
        CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(customer);
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customer)
                .items(null)
                .build();
        long orderId = entity.getId();

        orderPersistenceEntityRepository.saveAndFlush(entity);

        OrderPersistenceEntity persistedEntity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();
        OffsetDateTime firstLastModifiedAt = persistedEntity.getLastModifiedAt();
        var createdByUserId = persistedEntity.getCreatedByUserId();

        Thread.sleep(5);

        persistedEntity.setStatus("PAID");
        orderPersistenceEntityRepository.saveAndFlush(persistedEntity);

        OrderPersistenceEntity updatedEntity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();

        assertThat(updatedEntity.getCreatedByUserId()).isEqualTo(createdByUserId);
        assertThat(updatedEntity.getLastModifiedByUserId()).isNotNull();
        assertThat(updatedEntity.getLastModifiedAt()).isNotNull();
        assertThat(updatedEntity.getLastModifiedAt()).isAfterOrEqualTo(firstLastModifiedAt);
        assertThat(updatedEntity.getStatus()).isEqualTo("PAID");
    }

    @Test
    public void shouldPreventStaleUpdates() {
        CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(customer);
        OrderPersistenceEntity entity1 = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customer)
                .items(null)
                .build();
        long orderId = entity1.getId();
        orderPersistenceEntityRepository.saveAndFlush(entity1);

        // Now simulate two concurrent users get the same entity with same version
        OrderPersistenceEntity user1Entity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();
        OrderPersistenceEntity user2Entity = orderPersistenceEntityRepository.findById(orderId).orElseThrow();

        // Detach user2Entity so it's not in the persistence context
        entityManager.detach(user2Entity);

        // user1 updates and saves first
        user1Entity.setStatus("PLACED");
        orderPersistenceEntityRepository.saveAndFlush(user1Entity);

        // user2 tries to save with stale version
        user2Entity.setStatus("PAID");
        assertThatThrownBy(() -> orderPersistenceEntityRepository.saveAndFlush(user2Entity))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

}
