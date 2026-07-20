package com.eskcti.algashop.ordering.infrastructure.shipping.client.fake;

import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;

@Component
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "FAKE")
public class ShippingCostServiceFakeImpl implements ShippingCostService {

  @Override
  public CalculationResult calculate(CalculationRequest request) {
    return new CalculationResult(
        new Money("20"),
        LocalDate.now().plusDays(5));
  }
}
