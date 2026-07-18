package com.eskcti.algashop.ordering.domain.model.order;

import java.time.Year;
import java.util.List;

import com.eskcti.algashop.ordering.domain.model.Repository;
import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;

public interface Orders extends Repository<Order, OrderId> {

  List<Order> placedByCustomerInYear(CustomerId customerId, Year year);

  long salesQuantityByCustomerInYear(CustomerId customerId, Year year);

  Money totalSoldForCustomer(CustomerId customerId);
}
