package com.eskcti.algashop.ordering.domain.model.repository;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
  Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}