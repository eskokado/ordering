package com.eskcti.algashop.ordering.infrastructure.client.rapidex;

import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.service.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {

  private final RapiDexAPIClient rapiDexAPIClient;

  @Override
  public CalculationResult calculate(CalculationRequest request) {
    DeliveryCostResponse response = rapiDexAPIClient.calculate(
        new DeliveryCostRequest(
            request.origin().value(),
            request.destination().value()));

    LocalDate expectedDeliveryDate = LocalDate.now().plusDays(response.getEstimatedDaysToDeliver());

    return CalculationResult.builder()
        .cost(new Money(response.getDeliveryCost()))
        .expectedDate(expectedDeliveryDate)
        .build();
  }
}