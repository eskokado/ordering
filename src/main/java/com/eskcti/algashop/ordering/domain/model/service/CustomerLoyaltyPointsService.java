package com.eskcti.algashop.ordering.domain.model.service;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsOrderIsNotReady;
import com.eskcti.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.eskcti.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;

public class CustomerLoyaltyPointsService {

  private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);

  private static final Money expectedAmountToGivePoints = new Money("1000");

  public void addPoints(Customer customer, Order order) {
    Objects.requireNonNull(customer);
    Objects.requireNonNull(order);

    if (!customer.id().equals(order.customerId())) {
      throw new OrderNotBelongsToCustomerException();
    }

    if (!order.isReady()) {
      throw new CantAddLoyaltyPointsOrderIsNotReady();
    }

    LoyaltyPoints points = calculatePoints(order);
    if (points.value() > 0) {
      customer.addLoyaltyPoints(points);
    }
  }

  private LoyaltyPoints calculatePoints(Order order) {
    if (shouldGivePointsByAmount(order.totalAmount())) {
      Money result = order.totalAmount().divide(expectedAmountToGivePoints);
      return new LoyaltyPoints(result.value().intValue() * basePoints.value());
    }

    return LoyaltyPoints.ZERO;
  }

  private boolean shouldGivePointsByAmount(Money amount) {
    return amount.compareTo(expectedAmountToGivePoints) >= 0;
  }
}
