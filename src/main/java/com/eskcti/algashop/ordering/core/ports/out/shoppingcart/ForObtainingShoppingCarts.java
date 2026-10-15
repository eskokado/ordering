package com.eskcti.algashop.ordering.core.ports.out.shoppingcart;

import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;

import java.util.UUID;

public interface ForObtainingShoppingCarts {
    ShoppingCartOutput findById(UUID shoppingCartId);

    ShoppingCartOutput findByCustomerId(UUID customerId);
}
