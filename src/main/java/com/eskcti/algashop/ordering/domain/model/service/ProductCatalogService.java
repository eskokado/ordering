package com.eskcti.algashop.ordering.domain.model.service;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.valueobject.Product;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

public interface ProductCatalogService {
  Optional<Product> ofId(ProductId productId);
}
