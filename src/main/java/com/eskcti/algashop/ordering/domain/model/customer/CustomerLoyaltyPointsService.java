package com.eskcti.algashop.ordering.domain.model.customer;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.DomainService;
import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;

@DomainService
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
