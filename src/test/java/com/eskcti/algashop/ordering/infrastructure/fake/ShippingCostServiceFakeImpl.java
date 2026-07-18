package com.eskcti.algashop.ordering.infrastructure.fake;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;

@Component
public class ShippingCostServiceFakeImpl implements ShippingCostService {
  @Override
  public CalculationResult calculate(CalculationRequest request) {
    return new CalculationResult(
        new Money("20"),
        LocalDate.now().plusDays(5));
  }
}