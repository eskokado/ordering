package com.eskcti.algashop.ordering.core.application.shoppingcart;

import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ShoppingCartOutput;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartQueryService implements ForQueryingShoppingCarts {

    private final ForObtainingShoppingCarts forObtainingShoppingCarts;

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        return forObtainingShoppingCarts.findById(shoppingCartId);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        return forObtainingShoppingCarts.findByCustomerId(customerId);
    }
}
