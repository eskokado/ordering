package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.order.query.OrderDetailOutput;
import com.eskcti.algashop.ordering.application.order.query.OrderQueryService;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

  private final OrderPersistenceEntityRepository repository;
  private final Mapper mapper;

  @Override
  public OrderDetailOutput findById(String id) {
    OrderPersistenceEntity entity = repository.findById(new OrderId(id).value().toLong())
        .orElseThrow(() -> new OrderNotFoundException());
    return mapper.convert(entity, OrderDetailOutput.class);
  }
}