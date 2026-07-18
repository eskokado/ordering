package com.eskcti.algashop.ordering.infrastructure.fake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Product;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

class ProductCatalogServiceFakeImplTest {

  private final ProductCatalogServiceFakeImpl productCatalogService = new ProductCatalogServiceFakeImpl();

  @Test
  void shouldReturnFakeProductForGivenId() {
    ProductId productId = new ProductId();

    Product product = productCatalogService.ofId(productId).orElseThrow();

    assertThat(product.id()).isEqualTo(productId);
    assertThat(product.inStock()).isTrue();
    assertThat(product.name()).isEqualTo(new ProductName("Notebook"));
    assertThat(product.price()).isEqualTo(new Money("3000"));
  }

}
