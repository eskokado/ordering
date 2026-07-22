package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.application.customer.query.CustomerQueryService;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

  private final CustomerPersistenceEntityRepository repository;
  private final Mapper mapper;

  @Override
  public CustomerOutput findById(UUID customerId) {
    CustomerPersistenceEntity customer = repository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException());
    return mapper.convert(customer, CustomerOutput.class);
  }
}