package com.eskcti.algashop.ordering.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;

public interface CustomerPersistenceEntityRepository extends JpaRepository<CustomerPersistenceEntity, UUID> {

}
