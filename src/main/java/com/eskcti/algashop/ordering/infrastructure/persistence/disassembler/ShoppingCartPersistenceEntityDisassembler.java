package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
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