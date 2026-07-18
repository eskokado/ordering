package com.eskcti.algashop.ordering.domain.model.order.shipping;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.eskcti.algashop.ordering.infrastructure.client.rapidex.RapiDexAPIClientConfig;
import com.eskcti.algashop.ordering.infrastructure.client.rapidex.ShippingCostServiceRapidexImpl;
import com.eskcti.algashop.ordering.infrastructure.fake.FixedOriginAddressService;

@SpringBootTest(classes = {
    RapiDexAPIClientConfig.class,
    ShippingCostServiceRapidexImpl.class,
    FixedOriginAddressService.class
})
@TestPropertySource(properties = {
    "algashop.integrations.shipping.provider=RAPIDEX",
    "algashop.integrations.rapidex.url=http://localhost:8780"
})
class ShippingCostServiceIT {

  @Autowired
  private ShippingCostService shippingCostService;

  @Autowired
  private OriginAddressService originAddressService;

  @Test
  void shouldCalculateUsingRapidexWireMock() {
    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("54321");

    var result = shippingCostService
        .calculate(new CalculationRequest(origin, destination));

    Assertions.assertThat(result.cost()).isEqualTo(new Money("35.00"));
    Assertions.assertThat(result.expectedDate()).isEqualTo(LocalDate.now().plusDays(7));
  }

}
