package com.eskcti.algashop.ordering.domain.model.utility;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.utility.IdGenerator;

import org.assertj.core.api.Assertions;

class IdGeneratorTest {

  @Test
  void shouldGenerateTimeBasedUuid() {
    Assertions.assertThat(IdGenerator.generateTimeBasedUUID()).isNotNull();
  }

  @Test
  void shouldGenerateTsid() {
    Assertions.assertThat(IdGenerator.gererateTSID()).isNotNull();
  }
}
