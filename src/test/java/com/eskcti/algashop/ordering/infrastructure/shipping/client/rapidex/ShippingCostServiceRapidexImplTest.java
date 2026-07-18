package com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.DeliveryCostRequest;
import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.DeliveryCostResponse;
import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.RapiDexAPIClient;
import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.ShippingCostServiceRapidexImpl;

@ExtendWith(MockitoExtension.class)
class ShippingCostServiceRapidexImplTest {

  @Mock
  private RapiDexAPIClient rapiDexAPIClient;

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
}
