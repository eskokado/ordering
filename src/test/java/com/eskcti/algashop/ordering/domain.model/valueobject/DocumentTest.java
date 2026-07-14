package com.eskcti.algashop.ordering.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.valueobject.Document;

import org.assertj.core.api.Assertions;

class DocumentTest {

  @Test
  void shouldCreateWithValidValue() {
    Document document = new Document("255-08-0578");

    Assertions.assertThat(document.value()).isEqualTo("255-08-0578");
    Assertions.assertThat(document.toString()).isEqualTo("255-08-0578");
  }

  @Test
  void shouldNotCreateWithNullValue() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new Document(null));
  }

  @Test
  void shouldNotCreateWithBlankValue() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Document(""));
  }
}
