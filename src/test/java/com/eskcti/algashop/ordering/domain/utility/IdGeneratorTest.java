package com.eskcti.algashop.ordering.domain.utility;

import org.junit.jupiter.api.Test;
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
