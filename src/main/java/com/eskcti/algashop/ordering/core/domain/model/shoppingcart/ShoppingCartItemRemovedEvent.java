package com.eskcti.algashop.ordering.core.domain.model.shoppingcart;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductId;

public record ShoppingCartItemRemovedEvent(
        ShoppingCartId shoppingCartId,
        CustomerId customerId,
        ProductId productId,
        OffsetDateTime removedAt) {
}