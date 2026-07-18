package com.eskcti.algashop.ordering.infrastructure.shipping.client.fake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;

class FixedOriginAddressServiceTest {

  private final FixedOriginAddressService originAddressService = new FixedOriginAddressService();

  @Test
  void shouldReturnFixedOriginAddress() {
    var address = originAddressService.originAddress();

    assertThat(address.street()).isEqualTo("Bourbon Street");
    assertThat(address.number()).isEqualTo("1134");
    assertThat(address.zipCode()).isEqualTo(new ZipCode("12345"));
  }
}
