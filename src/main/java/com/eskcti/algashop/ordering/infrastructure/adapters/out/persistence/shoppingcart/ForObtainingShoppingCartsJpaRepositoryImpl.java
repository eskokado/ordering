package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.core.application.utility.Mapper;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ShoppingCartOutput;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ForObtainingShoppingCartsJpaRepositoryImpl implements ForObtainingShoppingCarts {

  private final ShoppingCartPersistenceEntityRepository persistenceRepository;
  private final Mapper mapper;

  @Override
  public ShoppingCartOutput findById(UUID shoppingCartId) {
    return persistenceRepository.findById(shoppingCartId)
        .map(s -> mapper.convert(s, ShoppingCartOutput.class))
        .orElseThrow(ShoppingCartNotFoundException::new);
  }

  @Override
  public ShoppingCartOutput findByCustomerId(UUID customerId) {
    return persistenceRepository.findByCustomer_Id(customerId)
        .map(s -> mapper.convert(s, ShoppingCartOutput.class))
        .orElseThrow(ShoppingCartNotFoundException::new);
  }
}