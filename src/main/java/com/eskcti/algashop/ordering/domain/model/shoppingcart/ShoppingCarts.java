package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.RemoveCapableRepository;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;

public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
  Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}