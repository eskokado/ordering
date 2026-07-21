package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;

public record ShoppingCartItemAddedEvent(
    ShoppingCartId shoppingCartId,
    CustomerId customerId,
    ProductId productId,
    OffsetDateTime addedAt) {
}