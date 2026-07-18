package com.eskcti.algashop.ordering.infrastructure.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainServiceScanConfigTest {

  @Test
  void shouldInstantiateConfiguration() {
    assertThat(new DomainServiceScanConfig()).isNotNull();
  }
}
