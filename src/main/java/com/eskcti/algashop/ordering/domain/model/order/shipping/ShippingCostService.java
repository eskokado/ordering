package com.eskcti.algashop.ordering.domain.model.order.shipping;

import java.time.LocalDate;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;

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