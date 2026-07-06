package com.eskcti.algashop.ordering.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;
import java.time.Duration;

import com.eskcti.algashop.ordering.domain.exception.ErrorMessages;

public record BirthDate(LocalDate value) {

  public BirthDate {
    Objects.requireNonNull(value);
    if (value.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
    }
  }

  public Integer age() {
    return (int) Duration.between(value, LocalDate.now()).toDays();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
