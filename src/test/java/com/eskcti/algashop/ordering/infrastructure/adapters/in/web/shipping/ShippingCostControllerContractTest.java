package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shipping;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.config.JsonConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;

import com.eskcti.algashop.ordering.core.application.shipping.ShippingApplicationService;
import com.eskcti.algashop.ordering.core.application.shipping.ShippingCostPreviewInput;
import com.eskcti.algashop.ordering.core.application.shipping.ShippingCostPreviewOutput;

@WebMvcTest(controllers = ShippingCostController.class)
class ShippingCostControllerContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ShippingApplicationService shippingApplicationService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssuredMockMvc.config = RestAssuredMockMvc.config()
                .jsonConfig(JsonConfig.jsonConfig()
                        .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    @Test
    void shouldReturnShippingCostPreview() {
        ShippingCostPreviewOutput output = new ShippingCostPreviewOutput(
                new BigDecimal("35.50"),
                LocalDate.of(2026, 9, 23)
        );

        when(shippingApplicationService.previewCost(any(ShippingCostPreviewInput.class)))
                .thenReturn(output);

        String jsonInput = """
                {
                  "zipCode": "12345"
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .when()
                .post("/api/v1/shipping-cost-previews")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(200)
                .body(
                        "cost", Matchers.comparesEqualTo(new BigDecimal("35.50")),
                        "expectedDate", Matchers.is("2026-09-23")
                );
    }

    @Test
    void shouldReturn400WhenZipCodeIsBlank() {
        String jsonInput = """
                {
                  "zipCode": ""
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .when()
                .post("/api/v1/shipping-cost-previews")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenZipCodeHasWrongSize() {
        String jsonInput = """
                {
                  "zipCode": "123"
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .when()
                .post("/api/v1/shipping-cost-previews")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenZipCodeIsMissing() {
        String jsonInput = """
                {}
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .when()
                .post("/api/v1/shipping-cost-previews")
                .then()
                .assertThat()
                .statusCode(400);
    }
}
