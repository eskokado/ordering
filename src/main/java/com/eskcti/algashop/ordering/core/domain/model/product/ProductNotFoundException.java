package com.eskcti.algashop.ordering.core.domain.model.product;

import com.eskcti.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.ErrorMessages;

public class ProductNotFoundException extends DomainEntityNotFoundException {
  public ProductNotFoundException() {

  }

  public ProductNotFoundException(ProductId productId) {
    super(String.format(ErrorMessages.ERROR_PRODUCT_NOT_FOUND, productId));
  }
}
