package com.eskcti.algashop.ordering.domain.model.service;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.repository.Customers;
import com.eskcti.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.eskcti.algashop.ordering.domain.model.utility.DomainService;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

  private final ShoppingCarts shoppingCarts;
  private final Customers customers;

  public ShoppingCart startShopping(CustomerId customerId) {
    if (!customers.exists(customerId)) {
      throw new CustomerNotFoundException();
    }

    if (shoppingCarts.ofCustomer(customerId).isPresent()) {
      throw new CustomerAlreadyHaveShoppingCartException();
    }

    return ShoppingCart.startShopping(customerId);
  }

}