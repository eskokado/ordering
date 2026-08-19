package com.eskcti.algashop.ordering.core.domain.model.customer;

import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.ErrorMessages;

public class CustomerEmailIsInUseException extends DomainException {
  public CustomerEmailIsInUseException() {
    super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE);
  }

  public CustomerEmailIsInUseException(CustomerId customerId) {
    super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE);
  }
}