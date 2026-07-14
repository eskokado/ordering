package com.eskcti.algashop.ordering.domain.model.entity;

import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Product;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

public class ProductTestDataBuilder {
  private ProductTestDataBuilder() {
  }

  public static Product.ProductBuilder aProduct() {
    return Product.builder()
        .id(new ProductId())
        .inStock(true)
        .name(new ProductName("Notebook X11"))
        .price(new Money("3000.00"));
  }

  public static Product.ProductBuilder aProductUnavailable() {
    return Product.builder()
        .id(new ProductId())
        .inStock(false)
        .name(new ProductName("Notebook X11"))
        .price(new Money("3000.00"));
  }

  public static Product.ProductBuilder aProductAltRamMemory() {
    return Product.builder()
        .id(new ProductId())
        .inStock(true)
        .name(new ProductName("4GB RAM"))
        .price(new Money("200.00"));
  }

  public static Product.ProductBuilder aProductAltMousePad() {
    return Product.builder()
        .id(new ProductId())
        .inStock(true)
        .name(new ProductName("Mouse Pad"))
        .price(new Money("100.00"));
  }
}
