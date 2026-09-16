package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;

@ExtendWith(MockitoExtension.class)
class ShippingCostServiceRapidexImplTest {

  @Mock
  private ResilientRapiDexAPIClient rapiDexAPIClient;

  @InjectMocks
  private ShippingCostServiceRapidexImpl shippingCostService;

  @Test
  void shouldCalculateShippingCostFromRapidexResponse() {
    CalculationRequest request = CalculationRequest.builder()
        .origin(new ZipCode("12345"))
        .destination(new ZipCode("54321"))
        .build();

    when(rapiDexAPIClient.calculate(new DeliveryCostRequest("12345", "54321")))
        .thenReturn(new DeliveryCostResponse("15.50", 3L));

    var result = shippingCostService.calculate(request);

    assertThat(result.cost()).isEqualTo(new Money("15.50"));
    assertThat(result.expectedDate()).isEqualTo(LocalDate.now().plusDays(3));
  }

  @Test
  void shouldThrowClientErrorWhenResponseIsNull() {
    CalculationRequest request = CalculationRequest.builder()
        .origin(new ZipCode("12345"))
        .destination(new ZipCode("54321"))
        .build();

    when(rapiDexAPIClient.calculate(new DeliveryCostRequest("12345", "54321")))
        .thenReturn(null);

    assertThatThrownBy(() -> shippingCostService.calculate(request))
        .isInstanceOf(BadGatewayException.ClientErrorException.class)
        .hasMessage("Rapidex API Client Error");
  }

  @Test
  void shouldThrowGatewayTimeoutWhenResilientClientThrowsTimeoutException() {
    CalculationRequest request = CalculationRequest.builder()
        .origin(new ZipCode("12345"))
        .destination(new ZipCode("54321"))
        .build();

    when(rapiDexAPIClient.calculate(new DeliveryCostRequest("12345", "54321")))
        .thenThrow(new GatewayTimeoutException("Rapidex API Timeout"));

    assertThatThrownBy(() -> shippingCostService.calculate(request))
        .isInstanceOf(GatewayTimeoutException.class)
        .hasMessage("Rapidex API Timeout");
  }

  @Test
  void shouldThrowBadGatewayWhenResilientClientThrowsBadGatewayException() {
    CalculationRequest request = CalculationRequest.builder()
        .origin(new ZipCode("12345"))
        .destination(new ZipCode("54321"))
        .build();

    when(rapiDexAPIClient.calculate(new DeliveryCostRequest("12345", "54321")))
        .thenThrow(new BadGatewayException.ServerErrorException("Rapidex API Bad Gateway", new RuntimeException()));

    assertThatThrownBy(() -> shippingCostService.calculate(request))
        .isInstanceOf(BadGatewayException.class)
        .hasMessage("Rapidex API Bad Gateway");
  }
}
