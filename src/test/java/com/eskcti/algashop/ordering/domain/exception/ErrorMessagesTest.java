package com.eskcti.algashop.ordering.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorMessagesTest {

  @Test
  void shouldInstantiateErrorMessagesClass() {
    assertThat(new ErrorMessages()).isNotNull();
  }

  @Test
  void shouldExposeValidationErrorMessages() {
    assertThat(ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID).isEqualTo("Email is invalid");
    assertThat(ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST).isEqualTo("BirthDate must be a past date");
    assertThat(ErrorMessages.VALIDATION_ERROR_FULLNAME_IS_NULL).isEqualTo("FullName cannot be null");
    assertThat(ErrorMessages.VALIDATION_ERROR_FULLNAME_IS_BLANK).isEqualTo("FullName cannot be blank");
  }

  @Test
  void shouldExposeDomainErrorMessages() {
    assertThat(ErrorMessages.ERROR_CUSTOMER_ARCHIVED).isEqualTo("Customer is archived it cannot be changed");
    assertThat(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED)
        .isEqualTo("Cannot change order %s status from %s to %s");
    assertThat(ErrorMessages.ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST)
        .isEqualTo("Order %s expected delivery date cannot be in the past");
    assertThat(ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK)
        .isEqualTo("Product %s is out of stock");
  }
}
