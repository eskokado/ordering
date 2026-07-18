package com.eskcti.algashop.ordering.domain.model;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID>
    extends Repository<T, ID> {
  void remove(T aggregateRoot);

  void remove(ID id);
}
