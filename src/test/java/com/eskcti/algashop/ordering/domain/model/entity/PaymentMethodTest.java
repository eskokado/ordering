
package com.eskcti.algashop.ordering.domain.model.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

  @Test
  void testValues() {
    assertThat(PaymentMethod.values()).hasSize(2);
    assertThat(PaymentMethod.CREDIT_CARD).isNotNull();
    assertThat(PaymentMethod.GATEWAY_BALANCE).isNotNull();
  }

}
