package com.eskcti.algashop.ordering.domain.model.order.shipping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@SpringBootTest

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
    wireMockRapidex = new WireMockServer(options()
        .port(8780)
        .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
        .extensions(new ResponseTemplateTransformer(true)));

    wireMockRapidex.start();
  }

  @Test
  void shouldCalculate() {
    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode("12345");

    var calculate = shippingCostService
        .calculate(new CalculationRequest(origin, destination));

    Assertions.assertThat(calculate.cost()).isNotNull();
    Assertions.assertThat(calculate.expectedDate()).isNotNull();
  }
}
