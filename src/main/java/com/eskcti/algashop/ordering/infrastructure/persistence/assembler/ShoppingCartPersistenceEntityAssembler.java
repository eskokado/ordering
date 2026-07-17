package com.eskcti.algashop.ordering.infrastructure.persistence.assembler;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

  private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

  public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
    return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
  }

  public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity,
      ShoppingCart shoppingCart) {
    persistenceEntity.setId(shoppingCart.id().value());
    persistenceEntity
        .setCustomer(customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value()));
    persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
    persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
    persistenceEntity.setCreatedAt(shoppingCart.createdAt());
    persistenceEntity.setVersion(shoppingCart.version());
    persistenceEntity.replaceItems(toOrderItemsEntities(shoppingCart.items()));
    return persistenceEntity;
  }

  private Set<ShoppingCartItemPersistenceEntity> toOrderItemsEntities(Set<ShoppingCartItem> source) {
    return source.stream().map(i -> this.mergeItem(new ShoppingCartItemPersistenceEntity(), i))
        .collect(Collectors.toSet());
  }

  private ShoppingCartItemPersistenceEntity mergeItem(ShoppingCartItemPersistenceEntity persistenceEntity,
      ShoppingCartItem shoppingCartItem) {
    persistenceEntity.setId(shoppingCartItem.id().value());
    persistenceEntity.setProductId(shoppingCartItem.productId().value());
    persistenceEntity.setName(shoppingCartItem.name().value());
    persistenceEntity.setPrice(shoppingCartItem.price().value());
    persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
    persistenceEntity.setAvailable(shoppingCartItem.isAvailable());
    persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
    return persistenceEntity;
  }
}