package com.eskcti.algashop.ordering.infrastructure.adapters.in.web;

import com.eskcti.algashop.ordering.utils.MockJwtDecoderConfig;
import com.eskcti.algashop.ordering.utils.MockJwtDecoderFactory;
import com.eskcti.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Import({ TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class })
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
public abstract class AbstractPresentationIT {

    private static final String WIREMOCK_ROOT = "src/test/resources/wiremock";

    private static final String RAPIDEX_FOLDER = WIREMOCK_ROOT + "/rapidex";

    private static final String PRODUCT_CATALOG_FOLDER = WIREMOCK_ROOT + "/product-catalog";

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockRapidex;
    protected static WireMockServer wireMockProductCatalog;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    @AfterEach
    public void stopWireMockServers() {
        stopMock();
    }

    protected RequestSpecification givenAuthenticated() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + MockJwtDecoderFactory.DEFAULT_TOKEN_VALUE);
    }

    protected static void initWireMock() {
        wireMockRapidex = new WireMockServer(options()
                .port(8780)
                .usingFilesUnderDirectory(RAPIDEX_FOLDER)
                .extensions(new ResponseTemplateTransformer(
                        TemplateEngine.defaultTemplateEngine(),
                        true,
                        new ClasspathFileSource(RAPIDEX_FOLDER),
                        Collections.emptyList())));

        wireMockProductCatalog = new WireMockServer(options()
                .port(8781)
                .usingFilesUnderDirectory(PRODUCT_CATALOG_FOLDER)
                .extensions(new ResponseTemplateTransformer(
                        TemplateEngine.defaultTemplateEngine(),
                        true,
                        new ClasspathFileSource(PRODUCT_CATALOG_FOLDER),
                        Collections.emptyList())));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    protected static void stopMock() {
        if (wireMockRapidex != null && wireMockRapidex.isRunning()) {
            wireMockRapidex.stop();
        }
        if (wireMockProductCatalog != null && wireMockProductCatalog.isRunning()) {
            wireMockProductCatalog.stop();
        }
    }

}
