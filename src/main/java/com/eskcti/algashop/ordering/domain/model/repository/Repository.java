package com.eskcti.algashop.ordering.domain.model.repository;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.entity.AggregateRoot;

public interface Repository<T extends AggregateRoot<ID>, ID> {
  Optional<T> ofId(ID id);

  boolean exists(ID id);

  void add(T aggregateRoot);

  int count();
}
