package com.eskcti.algashop.ordering.domain.model.customer;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.Repository;
import com.eskcti.algashop.ordering.domain.model.commons.Email;

public interface Customers extends Repository<Customer, CustomerId> {
  Optional<Customer> ofEmail(Email email);

  boolean isEmailUnique(Email email, CustomerId exceptCustomerId);
}
