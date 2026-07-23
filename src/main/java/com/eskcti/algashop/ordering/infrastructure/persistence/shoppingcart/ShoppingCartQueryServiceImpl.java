package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.eskcti.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

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