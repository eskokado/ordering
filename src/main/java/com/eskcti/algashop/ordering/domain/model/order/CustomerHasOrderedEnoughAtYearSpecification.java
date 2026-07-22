package com.eskcti.algashop.ordering.domain.model.order;

import java.time.Year;

import com.eskcti.algashop.ordering.domain.model.Specification;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerHasOrderedEnoughAtYearSpecification
    implements Specification<Customer> {

  private final Orders orders;
  private final long expectedOrderCount;

  @Override
  public boolean isSatisfiedBy(Customer customer) {
    return orders.salesQuantityByCustomerInYear(
        customer.id(),
        Year.now()) >= expectedOrderCount;
  }
}