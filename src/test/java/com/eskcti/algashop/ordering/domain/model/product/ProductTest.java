package com.eskcti.algashop.ordering.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import com.eskcti.algashop.ordering.domain.model.product.ProductOutOfStockException;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;
import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

  @Test
  void shouldCreateWithValidFields() {
    Product product = ProductTestDataBuilder.aProduct().build();

    assertThat(product.id()).isNotNull();
    assertThat(product.name()).isEqualTo(new ProductName("Notebook X11"));
    assertThat(product.price()).isEqualTo(new Money("3000.00"));
    assertThat(product.inStock()).isTrue();
  }

  @Test
  void shouldThrowWhenIdIsNull() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Product.builder()
            .id(null)
            .name(new ProductName("Notebook"))
            .price(new Money("10.00"))
            .inStock(true)
            .build());
  }

  @Test
  void shouldThrowWhenNameIsNull() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Product.builder()
            .id(new ProductId())
            .name(null)
            .price(new Money("10.00"))
            .inStock(true)
            .build());
  }

  @Test
  void shouldThrowWhenPriceIsNull() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Product.builder()
            .id(new ProductId())
            .name(new ProductName("Notebook"))
            .price(null)
            .inStock(true)
            .build());
  }

  @Test
  void shouldThrowWhenInStockIsNull() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Product.builder()
            .id(new ProductId())
            .name(new ProductName("Notebook"))
            .price(new Money("10.00"))
            .inStock(null)
            .build());
  }

  @Test
  void shouldNotThrowWhenCheckOutOfStockAndProductIsInStock() {
    Product product = ProductTestDataBuilder.aProduct().build();

    product.checkOutOfStock();
  }

  @Test
  void shouldThrowProductOutOfStockExceptionWhenCheckOutOfStockAndProductIsOutOfStock() {
    Product product = ProductTestDataBuilder.aProductUnavailable().build();

    Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
        .isThrownBy(product::checkOutOfStock)
        .withMessage(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, product.id()));
  }
}
