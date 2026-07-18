package com.eskcti.algashop.ordering.infrastructure.fake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.service.ShippingCostService.CalculationRequest;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;

import java.time.LocalDate;

class ShippingCostServiceFakeImplTest {

  private final ShippingCostServiceFakeImpl shippingCostService = new ShippingCostServiceFakeImpl();

  @Test
  void shouldReturnFixedShippingCost() {
    var result = shippingCostService.calculate(CalculationRequest.builder()
        .origin(new ZipCode("12345"))
        .destination(new ZipCode("54321"))
        .build());

    assertThat(result.cost()).isEqualTo(new Money("20"));
    assertThat(result.expectedDate()).isEqualTo(LocalDate.now().plusDays(5));
  }
}
