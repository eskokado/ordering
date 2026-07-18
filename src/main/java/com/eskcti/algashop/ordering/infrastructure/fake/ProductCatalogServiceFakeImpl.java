package com.eskcti.algashop.ordering.infrastructure.fake;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.service.ProductCatalogService;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Product;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

public class ProductCatalogServiceFakeImpl implements ProductCatalogService {
  @Override
  public Optional<Product> ofId(ProductId productId) {
    Product product = Product.builder().id(productId)
        .inStock(true)
        .name(new ProductName("Notebook"))
        .price(new Money("3000"))
        .build();
    return Optional.of(product);
  }
}
