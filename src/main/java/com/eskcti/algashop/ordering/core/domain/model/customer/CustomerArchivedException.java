package com.eskcti.algashop.ordering.core.domain.model.customer;

import static com.eskcti.algashop.ordering.core.domain.model.ErrorMessages.*;

import com.eskcti.algashop.ordering.core.domain.model.DomainException;

public class CustomerArchivedException extends DomainException {

  public CustomerArchivedException() {
    super(ERROR_CUSTOMER_ARCHIVED);
  }

  public CustomerArchivedException(Throwable cause) {
    super(ERROR_CUSTOMER_ARCHIVED, cause);
  }
}
