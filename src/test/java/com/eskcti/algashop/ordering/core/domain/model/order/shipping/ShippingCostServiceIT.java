package com.eskcti.algashop.ordering.core.domain.model.order.shipping;

import com.eskcti.algashop.ordering.core.domain.model.AbstractDomainIT;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import com.eskcti.algashop.ordering.core.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.github.tomakehurst.wiremock.WireMockServer;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@TestPropertySource(properties = "algashop.integrations.shipping.provider=RAPIDEX")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShippingCostServiceIT extends AbstractDomainIT {

  @Autowired
  private ShippingCostService shippingCostService;

  @Autowired
  private OriginAddressService originAddressService;

  private static WireMockServer wireMockRapidex;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    wireMockRapidex = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockRapidex.start();
    registry.add("algashop.integrations.rapidex.url",
        () -> "http://localhost:" + wireMockRapidex.port());
  }

  @AfterAll
  static void cleanupAll() {
    if (wireMockRapidex != null && wireMockRapidex.isRunning()) {
      wireMockRapidex.stop();
    }
  }

  @BeforeEach
  void resetStubs() {
    wireMockRapidex.resetAll();
  }

  @Test
  @Order(1)
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
  @Order(2)
  void shouldReturnFallbackWhenApiReturns500() {
    wireMockRapidex.stubFor(post(urlPathEqualTo("/api/delivery-cost"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"error\":\"Internal Server Error\"}")));

    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12346");

    var result = shippingCostService
        .calculate(new CalculationRequest(origin, destination));

    Assertions.assertThat(result.cost()).isNotNull();
    Assertions.assertThat(result.expectedDate()).isNotNull();
  }

  @Test
  @Order(3)
  @Timeout(60)
  void shouldReturnFallbackWhenApiIsSlow() {
    wireMockRapidex.stubFor(post(urlPathEqualTo("/api/delivery-cost"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"deliveryCost\":\"50.00\",\"estimatedDaysToDeliver\":\"10\"}")
            .withFixedDelay(15000)));

    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12347");

    var result = shippingCostService
        .calculate(new CalculationRequest(origin, destination));

    Assertions.assertThat(result.cost()).isNotNull();
    Assertions.assertThat(result.expectedDate()).isNotNull();
  }
}
