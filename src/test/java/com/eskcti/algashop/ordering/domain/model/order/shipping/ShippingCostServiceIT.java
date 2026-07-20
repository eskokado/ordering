package com.eskcti.algashop.ordering.domain.model.order.shipping;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;

@SpringBootTest
class ShippingCostServiceIT {

  @Autowired
  private ShippingCostService shippingCostService;

  @Autowired
  private OriginAddressService originAddressService;

  @Value("${algashop.integrations.shipping.provider}")
  private String shippingProvider;

  @Test
  void shouldCalculateShippingCost() {
    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("54321");

    var result = shippingCostService.calculate(new CalculationRequest(origin, destination));

    if ("FAKE".equals(shippingProvider)) {
      Assertions.assertThat(result.cost()).isEqualTo(new Money("20"));
      Assertions.assertThat(result.expectedDate()).isEqualTo(LocalDate.now().plusDays(5));
    } else {
      Assertions.assertThat(result.cost()).isEqualTo(new Money("35.00"));
      Assertions.assertThat(result.expectedDate()).isEqualTo(LocalDate.now().plusDays(7));
    }
  }
}
