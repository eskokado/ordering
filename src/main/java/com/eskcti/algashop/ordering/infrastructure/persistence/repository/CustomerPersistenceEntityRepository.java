package com.eskcti.algashop.ordering.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;

public interface CustomerPersistenceEntityRepository extends JpaRepository<CustomerPersistenceEntity, UUID> {
  Optional<CustomerPersistenceEntity> findByEmail(String email);

  boolean existsByEmailAndIdNot(String email, UUID customerId);

}
