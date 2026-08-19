package com.eskcti.algashop.ordering.core.domain.model.shoppingcart;

import com.eskcti.algashop.ordering.core.domain.model.DomainService;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customers;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

  private final ShoppingCarts shoppingCarts;
  private final Customers customers;

  public ShoppingCart startShopping(CustomerId customerId) {
    if (!customers.exists(customerId)) {
      throw new CustomerNotFoundException(customerId);
    }

    if (shoppingCarts.ofCustomer(customerId).isPresent()) {
      throw new CustomerAlreadyHaveShoppingCartException(customerId);
    }

    return ShoppingCart.startShopping(customerId);
  }

}