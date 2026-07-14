package com.eskcti.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

import com.eskcti.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;

import lombok.Builder;

@Builder
public record Product(
    ProductId id,
    ProductName name,
    Money price,
    Boolean inStock) {
  public Product {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    Objects.requireNonNull(price);
    Objects.requireNonNull(inStock);
  }

  public void checkOutOfStock() {
    if (isOutOfStock()) {
      throw new ProductOutOfStockException(this.id());
    }
  }

  private boolean isOutOfStock() {
    return !inStock();
  }
}