package com.eskcti.algashop.ordering.infrastructure.product.client.fake;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;

@Component
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
