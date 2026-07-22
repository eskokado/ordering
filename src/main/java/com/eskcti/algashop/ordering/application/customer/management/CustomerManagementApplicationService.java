package com.eskcti.algashop.ordering.application.customer.management;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.commons.AddressData;
import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.customer.BirthDate;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

  private final CustomerRegistrationService customerRegistration;
  private final Customers customers;

  @Transactional
  public UUID create(CustomerInput input) {
    Objects.requireNonNull(input);
    AddressData address = input.getAddress();

    Customer customer = customerRegistration.register(
        new FullName(input.getFirstName(), input.getLastName()),
        new BirthDate(input.getBirthDate()),
        new Email(input.getEmail()),
        new Phone(input.getPhone()),
        new Document(input.getDocument()),
        input.getPromotionNotificationsAllowed(),
        Address.builder()
            .zipCode(new ZipCode(address.getZipCode()))
            .state(address.getState())
            .city(address.getCity())
            .neighborhood(address.getNeighborhood())
            .street(address.getStreet())
            .number(address.getNumber())
            .complement(address.getComplement())
            .build());

    customers.add(customer);

    return customer.id().value();
  }

  @Transactional
  public void update(UUID rawCustomerId, CustomerUpdateInput input) {
    Objects.requireNonNull(input);
    Objects.requireNonNull(rawCustomerId);

    Customer customer = customers.ofId(new CustomerId(rawCustomerId))
        .orElseThrow(() -> new CustomerNotFoundException());

    customer.changeName(new FullName(input.getFirstName(), input.getLastName()));
    customer.changePhone(new Phone(input.getPhone()));

    if (Boolean.TRUE.equals(input.getPromotionNotificationsAllowed())) {
      customer.enablePromotionNotifications();
    } else {
      customer.disablePromotionNotifications();
    }

    AddressData address = input.getAddress();

    customer.changeAddress(Address.builder()
        .zipCode(new ZipCode(address.getZipCode()))
        .state(address.getState())
        .city(address.getCity())
        .neighborhood(address.getNeighborhood())
        .street(address.getStreet())
        .number(address.getNumber())
        .complement(address.getComplement())
        .build());

    customers.add(customer);
  }

  @Transactional
  public void archive(UUID rawCustomerId) {
    Objects.requireNonNull(rawCustomerId);
    Customer customer = customers.ofId(new CustomerId(rawCustomerId))
        .orElseThrow(() -> new CustomerNotFoundException());
    customer.archive();
    customers.add(customer);
  }

  @Transactional
  public void changeEmail(UUID rawCustomerId, String newEmail) {
    Objects.requireNonNull(rawCustomerId);
    Objects.requireNonNull(newEmail);
    Customer customer = customers.ofId(new CustomerId(rawCustomerId))
        .orElseThrow(() -> new CustomerNotFoundException());
    customerRegistration.changeEmail(customer, new Email(newEmail));
    customers.add(customer);
  }

}