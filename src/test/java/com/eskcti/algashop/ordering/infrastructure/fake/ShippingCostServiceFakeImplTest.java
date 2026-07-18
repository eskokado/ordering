package com.eskcti.algashop.ordering.infrastructure.fake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;

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
