package com.eskcti.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class AddressTest {

  @Test
  void shouldCreateWithValidValues() {
    Address address = ValueObjectTestFixtures.validAddress();

    Assertions.assertThat(address.street()).isEqualTo("Bourbon Street");
    Assertions.assertThat(address.number()).isEqualTo("1134");
    Assertions.assertThat(address.neighborhood()).isEqualTo("North Ville");
    Assertions.assertThat(address.city()).isEqualTo("York");
    Assertions.assertThat(address.state()).isEqualTo("South California");
    Assertions.assertThat(address.zipCode()).isEqualTo(new ZipCode("12345"));
    Assertions.assertThat(address.complement()).isEqualTo("Apt. 114");
  }

  @Test
  void shouldNotCreateWithBlankStreet() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().street("  ").build());
  }

  @Test
  void shouldNotCreateWithBlankNeighborhood() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().neighborhood("").build());
  }

  @Test
  void shouldNotCreateWithBlankCity() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().city(" ").build());
  }

  @Test
  void shouldNotCreateWithBlankNumber() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().number("").build());
  }

  @Test
  void shouldNotCreateWithBlankState() {
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().state(" ").build());
  }

  @Test
  void shouldNotCreateWithNullZipCode() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> ValueObjectTestFixtures.validAddress().toBuilder().zipCode(null).build());
  }
}
