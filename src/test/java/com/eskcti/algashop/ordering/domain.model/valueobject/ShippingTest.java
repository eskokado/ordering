package com.eskcti.algashop.ordering.domain.model.valueobject;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.valueobject.FullName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Recipient;
import com.eskcti.algashop.ordering.domain.model.valueobject.Shipping;

import org.assertj.core.api.Assertions;

class ShippingTest {

  @Test
  void shouldCreateWithValidValues() {
    Shipping shipping = OrderTestDataBuilder.aShipping();

    Assertions.assertThat(shipping.cost()).isEqualTo(new Money("10.00"));
    Assertions.assertThat(shipping.expectedDate()).isAfter(LocalDate.now());
    Assertions.assertThat(shipping.recipient().fullName()).isEqualTo(new FullName("John", "Doe"));
    Assertions.assertThat(shipping.address()).isEqualTo(OrderTestDataBuilder.anAddress());
  }

  @Test
  void shouldNotCreateWithNullCost() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Shipping.builder()
            .cost(null)
            .expectedDate(LocalDate.now().plusDays(1))
            .recipient(aRecipient())
            .address(OrderTestDataBuilder.anAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullExpectedDate() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Shipping.builder()
            .cost(new Money("10.00"))
            .expectedDate(null)
            .recipient(aRecipient())
            .address(OrderTestDataBuilder.anAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullRecipient() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Shipping.builder()
            .cost(new Money("10.00"))
            .expectedDate(LocalDate.now().plusDays(1))
            .recipient(null)
            .address(OrderTestDataBuilder.anAddress())
            .build());
  }

  @Test
  void shouldNotCreateWithNullAddress() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> Shipping.builder()
            .cost(new Money("10.00"))
            .expectedDate(LocalDate.now().plusDays(1))
            .recipient(aRecipient())
            .address(null)
            .build());
  }

  private static Recipient aRecipient() {
    return Recipient.builder()
        .fullName(ValueObjectTestFixtures.validFullName())
        .document(ValueObjectTestFixtures.validDocument())
        .phone(ValueObjectTestFixtures.validPhone())
        .build();
  }
}
