package com.eskcti.algashop.ordering.domain.model.service;

import java.time.LocalDate;

import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;

import lombok.Builder;

public interface ShippingCostService {
  CalculationResult calculate(CalculationRequest request);

  @Builder
  record CalculationRequest(ZipCode origin, ZipCode destination) {
  }

  @Builder
  record CalculationResult(Money cost, LocalDate expectedDate) {
  }
}