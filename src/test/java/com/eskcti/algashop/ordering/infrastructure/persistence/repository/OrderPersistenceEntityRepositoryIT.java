package com.eskcti.algashop.ordering.infrastructure.persistence.repository;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
    }

    @Test
    public void shouldPersist() {
        long orderId = com.eskcti.algashop.ordering.domain.model.utility.IdGenerator.gererateTSID().toLong();
        OrderPersistenceEntity entity = OrderPersistenceEntity.builder()
                .id(orderId)
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertTrue(orderPersistenceEntityRepository.existsById(orderId));
    }

    @Test
    public void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        Assertions.assertEquals(0, ordersCount);
    }

}
