package com.eskcti.algashop.ordering.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;

public interface OrderPersistenceEntityRepository extends JpaRepository<OrderPersistenceEntity, Long> {
}