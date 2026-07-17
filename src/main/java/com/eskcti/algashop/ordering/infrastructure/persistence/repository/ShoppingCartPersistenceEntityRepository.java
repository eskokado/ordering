package com.eskcti.algashop.ordering.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;

public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {
  Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(UUID value);
}