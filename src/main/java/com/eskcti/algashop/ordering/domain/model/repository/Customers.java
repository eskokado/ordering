package com.eskcti.algashop.ordering.domain.model.repository;

import java.util.Optional;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.valueobject.Email;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;

public interface Customers extends Repository<Customer, CustomerId> {
  Optional<Customer> ofEmail(Email email);

  boolean isEmailUnique(Email email, CustomerId exceptCustomerId);
}
