package com.eskcti.algashop.ordering.core.application.customer.query;

import java.util.UUID;

import org.springframework.data.domain.Page;

public interface CustomerQueryService {
  CustomerOutput findById(UUID customerId);

  Page<CustomerSummaryOutput> filter(CustomerFilter filter);
}