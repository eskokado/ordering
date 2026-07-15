package com.eskcti.algashop.ordering.infrastructure.persistence.provider;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.repository.Orders;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {

  private final OrderPersistenceEntityRepository persistenceRepository;
  private final OrderPersistenceEntityAssembler assembler;
  private final OrderPersistenceEntityDisassembler disassembler;

  @Override
  public Optional<Order> ofId(OrderId orderId) {
    return persistenceRepository.findById(orderId.value().toLong())
        .map(disassembler::toDomainEntity);
  }

  @Override
  public boolean exists(OrderId orderId) {
    return persistenceRepository.existsById(orderId.value().toLong());
  }

  @Override
  public void add(Order aggregateRoot) {
    OrderPersistenceEntity persistenceEntity;
    if (aggregateRoot.version() != null) {
      // If the domain entity has a version, use that for optimistic locking
      persistenceEntity = new OrderPersistenceEntity();
    } else {
      // Else, check if the entity exists in the database
      persistenceEntity = persistenceRepository.findById(aggregateRoot.id().value().toLong())
          .orElseGet(() -> new OrderPersistenceEntity());
    }
    persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
    persistenceRepository.saveAndFlush(persistenceEntity);
  }

  @Override
  public int count() {
    return (int) persistenceRepository.count();
  }
}
