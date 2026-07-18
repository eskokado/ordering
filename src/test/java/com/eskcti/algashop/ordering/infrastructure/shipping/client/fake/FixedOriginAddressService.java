package com.eskcti.algashop.ordering.infrastructure.shipping.client.fake;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.OriginAddressService;

@Component
public class FixedOriginAddressService implements OriginAddressService {

  @Override
  public Address originAddress() {
    return Address.builder()
        .street("Bourbon Street")
        .number("1134")
        .neighborhood("North Ville")
        .city("York")
        .state("South California")
        .zipCode(new ZipCode("12345"))
        .build();
  }
}