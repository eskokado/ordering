package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerPersistenceEntityRepository
    extends JpaRepository<CustomerPersistenceEntity, UUID>, CustomerPersistenceEntityQueries {
  Optional<CustomerPersistenceEntity> findByEmail(String email);

  boolean existsByEmailAndIdNot(String email, UUID customerId);

}
