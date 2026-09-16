package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {

  private final ResilientRapiDexAPIClient rapiDexAPIClient;

  @Override
  public CalculationResult calculate(CalculationRequest request) {
        DeliveryCostResponse response = rapiDexAPIClient.calculate(
                new DeliveryCostRequest(
                        request.origin().value(),
                        request.destination().value()
                )
        );

        if (response == null) {
            throw new BadGatewayException.ClientErrorException("Rapidex API Client Error");
        }

        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(response.getEstimatedDaysToDeliver());

        return CalculationResult.builder()
                .cost(new Money(response.getDeliveryCost()))
                .expectedDate(expectedDeliveryDate)
                .build();
  }
}