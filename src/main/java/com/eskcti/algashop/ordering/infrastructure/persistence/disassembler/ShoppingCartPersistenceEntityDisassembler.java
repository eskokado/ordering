package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;

@Component
public class ShoppingCartPersistenceEntityDisassembler {
  public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity source) {
    return ShoppingCart.existing()
        .id(new ShoppingCartId(source.getId()))
        .version(source.getVersion())
        .customerId(new CustomerId(source.getCustomerId()))
        .totalAmount(new Money(source.getTotalAmount()))
        .createdAt(source.getCreatedAt())
        .items(toItemsDomainEntities(source.getItems()))
        .totalItems(new Quantity(source.getTotalItems()))
        .build();
  }

  private Set<ShoppingCartItem> toItemsDomainEntities(Set<ShoppingCartItemPersistenceEntity> source) {
    return source.stream().map(this::toItemEntity).collect(Collectors.toSet());
  }

  private ShoppingCartItem toItemEntity(ShoppingCartItemPersistenceEntity source) {
    return ShoppingCartItem.existing()
        .id(new ShoppingCartItemId(source.getId()))
        .shoppingCartId(new ShoppingCartId(source.getShoppingCartId()))
        .productId(new ProductId(source.getProductId()))
        .productName(new ProductName(source.getName()))
        .price(new Money(source.getPrice()))
        .quantity(new Quantity(source.getQuantity()))
        .available(source.getAvailable())
        .totalAmount(new Money(source.getTotalAmount()))
        .build();
  }
}