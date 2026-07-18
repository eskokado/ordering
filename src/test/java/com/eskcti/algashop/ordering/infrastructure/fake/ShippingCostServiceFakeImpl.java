package com.eskcti.algashop.ordering.infrastructure.fake;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.service.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;

@Component
public class ShippingCostServiceFakeImpl implements ShippingCostService {
  @Override
  public CalculationResult calculate(CalculationRequest request) {
    return new CalculationResult(
        new Money("20"),
        LocalDate.now().plusDays(5));
  }
}