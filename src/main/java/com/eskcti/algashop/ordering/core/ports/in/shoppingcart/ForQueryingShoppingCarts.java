package com.eskcti.algashop.ordering.core.ports.in.shoppingcart;

import java.util.UUID;

import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ShoppingCartOutput;

public interface ForQueryingShoppingCarts {
    ShoppingCartOutput findById(UUID shoppingCartId);

    ShoppingCartOutput findByCustomerId(UUID customerId);
}
