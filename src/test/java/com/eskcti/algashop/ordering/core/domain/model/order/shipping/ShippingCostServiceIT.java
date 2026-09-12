package com.eskcti.algashop.ordering.core.domain.model.order.shipping;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.core.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import com.github.tomakehurst.wiremock.WireMockServer;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest
@TestPropertySource(properties = "algashop.integrations.shipping.provider=RAPIDEX")
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class ShippingCostServiceIT {

  @Autowired
  private ShippingCostService shippingCostService;

  @Autowired
  private OriginAddressService originAddressService;

  private WireMockServer wireMockRapidex;

  @BeforeEach
  public void setup() {
    initWireMock();
  }

  @AfterEach
  public void clean() {
    wireMockRapidex.stop();
  }

  private void initWireMock() {
    wireMockRapidex = new WireMockServer(WireMockConfiguration.options()
        .port(8780));

    wireMockRapidex.start();
  }

  @Test
  void shouldCalculate() {
    wireMockRapidex.stubFor(post(urlPathEqualTo("/api/delivery-cost"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"deliveryCost\":\"35.00\",\"estimatedDaysToDeliver\":\"7\"}")));

    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12345");

    var calculate = shippingCostService
        .calculate(new CalculationRequest(origin, destination));

    Assertions.assertThat(calculate.cost()).isNotNull();
    Assertions.assertThat(calculate.expectedDate()).isNotNull();
  }

  @Test
  void shouldThrowBadGatewayWhenApiReturns500() {
    wireMockRapidex.stubFor(post(urlPathEqualTo("/api/delivery-cost"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"error\":\"Internal Server Error\"}")));

    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12346");

    Assertions.assertThatThrownBy(() ->
            shippingCostService.calculate(new CalculationRequest(origin, destination)))
        .isInstanceOf(BadGatewayException.class);
  }

  @Test
  void shouldThrowGatewayTimeoutWhenApiIsSlow() {
    wireMockRapidex.stubFor(post(urlPathEqualTo("/api/delivery-cost"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"deliveryCost\":\"50.00\",\"estimatedDaysToDeliver\":\"10\"}")
            .withFixedDelay(15000)));

    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12347");

    Assertions.assertThatThrownBy(() ->
            shippingCostService.calculate(new CalculationRequest(origin, destination)))
        .isInstanceOf(GatewayTimeoutException.class);
  }
}
