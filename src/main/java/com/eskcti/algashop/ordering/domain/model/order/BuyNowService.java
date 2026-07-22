package com.eskcti.algashop.ordering.domain.model.order;

import java.time.Year;

import com.eskcti.algashop.ordering.domain.model.DomainService;
import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.eskcti.algashop.ordering.domain.model.product.Product;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

  private final Orders orders;

  public Order buyNow(Product product,
      Customer customer,
      Billing billing,
      Shipping shipping,
      Quantity quantity,
      PaymentMethod paymentMethod) {

    if (quantity.compareTo(Quantity.ZERO) <= 0) {
      throw new IllegalArgumentException();
    }

    product.checkOutOfStock();

    Order order = Order.draft(customer.id());
    order.changeBilling(billing);
    order.changePaymentMethod(paymentMethod);

    if (haveFreeShipping(customer)) {
      Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
      order.changeShipping(freeShipping);
    } else {
      order.changeShipping(shipping);
    }

    order.addItem(product, quantity);
    order.place();

    return order;
  }

  private boolean haveFreeShipping(Customer customer) {
    return customer.loyaltyPoints().compareTo(new LoyaltyPoints(100)) >= 0
        && orders.salesQuantityByCustomerInYear(customer.id(), Year.now()) >= 2
        || customer.loyaltyPoints().compareTo(new LoyaltyPoints(2000)) >= 0;
  }

}