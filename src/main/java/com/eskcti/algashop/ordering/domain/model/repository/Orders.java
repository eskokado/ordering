package com.eskcti.algashop.ordering.domain.model.repository;

import java.time.Year;
import java.util.List;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;

public interface Orders extends Repository<Order, OrderId> {

  List<Order> placedByCustomerInYear(CustomerId customerId, Year year);

  long salesQuantityByCustomerInYear(CustomerId customerId, Year year);

  Money totalSoldForCustomer(CustomerId customerId);
}
