package com.eskcti.algashop.ordering.core.domain.model.customer;

import java.util.Optional;

import com.eskcti.algashop.ordering.core.domain.model.Repository;
import com.eskcti.algashop.ordering.core.domain.model.commons.Email;

public interface Customers extends Repository<Customer, CustomerId> {
  Optional<Customer> ofEmail(Email email);

  boolean isEmailUnique(Email email, CustomerId exceptCustomerId);
}
