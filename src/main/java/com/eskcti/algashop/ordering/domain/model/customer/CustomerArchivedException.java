package com.eskcti.algashop.ordering.domain.model.customer;

import static com.eskcti.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_ARCHIVED;

import com.eskcti.algashop.ordering.domain.model.DomainException;

public class CustomerArchivedException extends DomainException {

  public CustomerArchivedException() {
    super(ERROR_CUSTOMER_ARCHIVED);
  }

  public CustomerArchivedException(Throwable cause) {
    super(ERROR_CUSTOMER_ARCHIVED, cause);
  }
}
