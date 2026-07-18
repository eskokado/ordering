package com.eskcti.algashop.ordering.domain.model.product;

import com.eskcti.algashop.ordering.domain.model.DomainException;
import com.eskcti.algashop.ordering.domain.model.ErrorMessages;

public class ProductOutOfStockException extends DomainException {

  public ProductOutOfStockException(ProductId id) {
    super(String.format(ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK, id));
  }
}