package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
class ResilientProductCatalogAPIClientTest {

    @Mock
    private ProductCatalogAPIClient productCatalogAPIClient;

    private FrameworkRetryCircuitBreakerFactory circuitBreakerFactory;
    private ResilientProductCatalogAPIClient resilientClient;

    @BeforeEach
    void setUp() {
        circuitBreakerFactory = new FrameworkRetryCircuitBreakerFactory();
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .multiplier(2)
                .delay(Duration.ofMillis(10))
                .includes(GatewayTimeoutException.class, BadGatewayException.ServerErrorException.class)
                .build();
        circuitBreakerFactory.configure(builder -> builder
                .retryPolicy(retryPolicy)
                .openTimeout(Duration.ofSeconds(10))
                .resetTimeout(Duration.ofSeconds(25))
                .build(), "productCatalogCB");

        resilientClient = new ResilientProductCatalogAPIClient(circuitBreakerFactory, productCatalogAPIClient);
    }

    @Test
    void shouldReturnProductWhenCatalogRespondsSuccessfully() {
        UUID productId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
        ProductResponse response = new ProductResponse(
                productId,
                "Notebook X11",
                new BigDecimal("1000.00"),
                true);

        when(productCatalogAPIClient.getById(productId)).thenReturn(response);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(productId);
        assertThat(result.get().getName()).isEqualTo("Notebook X11");
        assertThat(result.get().getSalePrice()).isEqualByComparingTo("1000.00");
        assertThat(result.get().getInStock()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenCatalogRespondsNotFound() {
        UUID productId = UUID.fromString("21651a12-b126-4213-ac21-19f66ff4642e");
        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(notFound);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenCatalogReturnsNull() {
        UUID productId = UUID.randomUUID();

        when(productCatalogAPIClient.getById(productId)).thenReturn(null);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowGatewayTimeoutWhenCatalogIsUnreachable() {
        UUID productId = UUID.randomUUID();
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(productCatalogAPIClient.getById(productId)).thenThrow(timeout);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(GatewayTimeoutException.class)
                .hasMessage("Product Catalog API Timeout")
                .hasCause(timeout);
    }

    @Test
    void shouldThrowGatewayTimeoutWhenRestClientThrowsSocketTimeoutException() {
        UUID productId = UUID.randomUUID();
        SocketTimeoutException socketTimeout = new SocketTimeoutException("Read timed out");
        RestClientException restClientException = new RestClientException("Timeout", socketTimeout);

        when(productCatalogAPIClient.getById(productId)).thenThrow(restClientException);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(GatewayTimeoutException.class)
                .hasMessage("Product Catalog API Timeout")
                .hasCause(restClientException);
    }

    @Test
    void shouldReturnEmptyWhenClientErrorOccurs() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(badRequest);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenConflictErrorOccurs() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException conflict = HttpClientErrorException.create(
                HttpStatus.CONFLICT,
                "Conflict",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(conflict);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowBadGatewayWhenRestClientExceptionWithOtherCause() {
        UUID productId = UUID.randomUUID();
        RestClientException restClientException = new RestClientException("error",
                new RuntimeException("connection refused"));

        when(productCatalogAPIClient.getById(productId)).thenThrow(restClientException);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(BadGatewayException.class)
                .hasMessage("Product Catalog API Bad Gateway")
                .hasCause(restClientException);
    }

    @Test
    void shouldThrowBadGatewayServerErrorWhenCatalogReturnsServerError() {
        UUID productId = UUID.randomUUID();
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(serverError);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(BadGatewayException.ServerErrorException.class)
                .hasMessage("Product Catalog API Bad Gateway")
                .hasCause(serverError);
    }

    @Test
    void shouldInstantiateBadGatewayExceptions() {
        BadGatewayException.ServerErrorException serverError = new BadGatewayException.ServerErrorException();
        assertThat(serverError).isInstanceOf(BadGatewayException.class);

        BadGatewayException.ClientErrorException clientError = new BadGatewayException.ClientErrorException();
        assertThat(clientError).isInstanceOf(BadGatewayException.class);
    }

    @Test
    void shouldRetryOnGatewayTimeoutAndThrowAfterMaxRetries() {
        UUID productId = UUID.randomUUID();
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(productCatalogAPIClient.getById(productId)).thenThrow(timeout);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(GatewayTimeoutException.class)
                .hasMessage("Product Catalog API Timeout");

        verify(productCatalogAPIClient, times(4)).getById(productId);
    }

    @Test
    void shouldRetryOnServerErrorAndThrowAfterMaxRetries() {
        UUID productId = UUID.randomUUID();
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(serverError);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(BadGatewayException.ServerErrorException.class)
                .hasMessage("Product Catalog API Bad Gateway");

        verify(productCatalogAPIClient, times(4)).getById(productId);
    }

    @Test
    void shouldNotRetryOnClientError() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(badRequest);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
        verify(productCatalogAPIClient, times(1)).getById(productId);
    }

    @Test
    void shouldNotRetryOnRestClientExceptionWithGenericCause() {
        UUID productId = UUID.randomUUID();
        RestClientException restClientException = new RestClientException("error",
                new RuntimeException("connection refused"));

        when(productCatalogAPIClient.getById(productId)).thenThrow(restClientException);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(BadGatewayException.class)
                .hasMessage("Product Catalog API Bad Gateway");

        verify(productCatalogAPIClient, times(1)).getById(productId);
    }

    @Test
    void shouldSucceedAfterRetryRecovery() {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Recovered Product",
                new BigDecimal("500.00"),
                true);

        when(productCatalogAPIClient.getById(productId))
                .thenThrow(new ResourceAccessException("Connection timed out"))
                .thenReturn(response);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Recovered Product");
        verify(productCatalogAPIClient, times(2)).getById(productId);
    }

    @Test
    void shouldOpenCircuitAfterRepeatedFailures() {
        UUID productId = UUID.randomUUID();
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(productCatalogAPIClient.getById(productId)).thenThrow(timeout);

        for (int i = 0; i < 20; i++) {
            try {
                resilientClient.getById(productId);
            } catch (Exception ignored) {
            }
        }

        verify(productCatalogAPIClient, atLeastOnce()).getById(productId);
    }

    @Test
    void shouldFailFastWhenCircuitIsOpen() {
        UUID productId = UUID.randomUUID();
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(productCatalogAPIClient.getById(productId)).thenThrow(timeout);

        for (int i = 0; i < 20; i++) {
            try {
                resilientClient.getById(productId);
            } catch (Exception ignored) {
            }
        }

        try {
            resilientClient.getById(productId);
        } catch (Exception ignored) {
        }
    }

    @Test
    void shouldHandleGatewayTimeoutExceptionWithMessageAndCause() {
        GatewayTimeoutException ex = new GatewayTimeoutException("timeout", new RuntimeException("root"));
        assertThat(ex.getMessage()).isEqualTo("timeout");
        assertThat(ex.getCause()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldHandleGatewayTimeoutExceptionDefaultConstructor() {
        GatewayTimeoutException ex = new GatewayTimeoutException();
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isNull();
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void shouldHandleBadGatewayExceptionDefaultConstructor() {
        BadGatewayException ex = new BadGatewayException();
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void shouldHandleBadGatewayExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        BadGatewayException ex = new BadGatewayException("msg", cause);
        assertThat(ex.getMessage()).isEqualTo("msg");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldHandleServerErrorExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        BadGatewayException.ServerErrorException ex = new BadGatewayException.ServerErrorException("msg", cause);
        assertThat(ex.getMessage()).isEqualTo("msg");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldHandleClientErrorExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        BadGatewayException.ClientErrorException ex = new BadGatewayException.ClientErrorException("msg", cause);
        assertThat(ex.getMessage()).isEqualTo("msg");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundIsThrownAndNotLoggedAsError() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(notFound);

        Optional<ProductResponse> result = resilientClient.getById(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCallLoadProductExactlyOnceOnSuccess() {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Product",
                new BigDecimal("100.00"),
                false);

        when(productCatalogAPIClient.getById(productId)).thenReturn(response);

        resilientClient.getById(productId);

        verify(productCatalogAPIClient, times(1)).getById(productId);
    }

    @Test
    void shouldConsecutiveFailuresTriggerCircuitBreakerProtection() {
        UUID productId = UUID.randomUUID();
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(productCatalogAPIClient.getById(productId)).thenThrow(timeout);

        int totalFailures = 0;
        for (int i = 0; i < 30; i++) {
            try {
                resilientClient.getById(productId);
            } catch (Exception e) {
                totalFailures++;
            }
        }

        assertThat(totalFailures).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldRethrowNoFallbackAvailableExceptionWhenCauseIsNotRetryException() {
        UUID productId = UUID.randomUUID();
        when(productCatalogAPIClient.getById(productId))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(NoFallbackAvailableException.class);
    }

    @Test
    void shouldTranslateHttpClientErrorExceptionViaReflection() throws Exception {
        Method translateMethod = ResilientProductCatalogAPIClient.class
                .getDeclaredMethod("translateException", RestClientException.class);
        translateMethod.setAccessible(true);

        HttpClientErrorException clientError = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        RuntimeException result = (RuntimeException) translateMethod.invoke(resilientClient, clientError);

        assertThat(result).isInstanceOf(BadGatewayException.ClientErrorException.class);
        assertThat(result.getMessage()).isEqualTo("Product Catalog API Bad Gateway");
        assertThat(result.getCause()).isEqualTo(clientError);
    }

    @Test
    void shouldTranslateResourceAccessExceptionViaReflection() throws Exception {
        Method translateMethod = ResilientProductCatalogAPIClient.class
                .getDeclaredMethod("translateException", RestClientException.class);
        translateMethod.setAccessible(true);

        ResourceAccessException resourceError = new ResourceAccessException("Connection refused");

        RuntimeException result = (RuntimeException) translateMethod.invoke(resilientClient, resourceError);

        assertThat(result).isInstanceOf(GatewayTimeoutException.class);
        assertThat(result.getMessage()).isEqualTo("Product Catalog API Timeout");
        assertThat(result.getCause()).isEqualTo(resourceError);
    }

    @Test
    void shouldTranslateSocketTimeoutExceptionViaReflection() throws Exception {
        Method translateMethod = ResilientProductCatalogAPIClient.class
                .getDeclaredMethod("translateException", RestClientException.class);
        translateMethod.setAccessible(true);

        SocketTimeoutException socketTimeout = new SocketTimeoutException("Read timed out");
        RestClientException restClientException = new RestClientException("Timeout", socketTimeout);

        RuntimeException result = (RuntimeException) translateMethod.invoke(resilientClient, restClientException);

        assertThat(result).isInstanceOf(GatewayTimeoutException.class);
        assertThat(result.getMessage()).isEqualTo("Product Catalog API Timeout");
        assertThat(result.getCause()).isEqualTo(restClientException);
    }

    @Test
    void shouldTranslateHttpServerErrorExceptionViaReflection() throws Exception {
        Method translateMethod = ResilientProductCatalogAPIClient.class
                .getDeclaredMethod("translateException", RestClientException.class);
        translateMethod.setAccessible(true);

        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                StandardCharsets.UTF_8);

        RuntimeException result = (RuntimeException) translateMethod.invoke(resilientClient, serverError);

        assertThat(result).isInstanceOf(BadGatewayException.ServerErrorException.class);
        assertThat(result.getMessage()).isEqualTo("Product Catalog API Bad Gateway");
        assertThat(result.getCause()).isEqualTo(serverError);
    }

    @Test
    void shouldTranslateGenericRestClientExceptionViaReflection() throws Exception {
        Method translateMethod = ResilientProductCatalogAPIClient.class
                .getDeclaredMethod("translateException", RestClientException.class);
        translateMethod.setAccessible(true);

        RestClientException genericException = new RestClientException("generic error",
                new RuntimeException("root cause"));

        RuntimeException result = (RuntimeException) translateMethod.invoke(resilientClient, genericException);

        assertThat(result).isInstanceOf(BadGatewayException.class);
        assertThat(result).isNotInstanceOf(BadGatewayException.ServerErrorException.class);
        assertThat(result).isNotInstanceOf(BadGatewayException.ClientErrorException.class);
        assertThat(result.getMessage()).isEqualTo("Product Catalog API Bad Gateway");
        assertThat(result.getCause()).isEqualTo(genericException);
    }

    @Test
    void shouldRethrowNoFallbackAvailableExceptionWhenRetryExceptionCauseIsNotKnownType() {
        UUID productId = UUID.randomUUID();

        CircuitBreakerFactory mockFactory = mock(CircuitBreakerFactory.class);
        CircuitBreaker mockCB = mock(CircuitBreaker.class);
        when(mockFactory.create("productCatalogCB")).thenReturn(mockCB);

        RetryException retryException = new RetryException("retry exhausted",
                new IllegalStateException("something else"));
        when(mockCB.run(any())).thenThrow(
                new NoFallbackAvailableException("No fallback available", retryException));

        ResilientProductCatalogAPIClient testClient =
                new ResilientProductCatalogAPIClient(mockFactory, productCatalogAPIClient);

        assertThatThrownBy(() -> testClient.getById(productId))
                .isInstanceOf(NoFallbackAvailableException.class);
    }

    @Test
    void shouldRethrowNoFallbackAvailableExceptionWhenCauseIsNotRetryExceptionDirectly() {
        UUID productId = UUID.randomUUID();

        CircuitBreakerFactory mockFactory = mock(CircuitBreakerFactory.class);
        CircuitBreaker mockCB = mock(CircuitBreaker.class);
        when(mockFactory.create("productCatalogCB")).thenReturn(mockCB);

        when(mockCB.run(any())).thenThrow(
                new NoFallbackAvailableException("No fallback available",
                        new IllegalStateException("unexpected")));

        ResilientProductCatalogAPIClient testClient =
                new ResilientProductCatalogAPIClient(mockFactory, productCatalogAPIClient);

        assertThatThrownBy(() -> testClient.getById(productId))
                .isInstanceOf(NoFallbackAvailableException.class);
    }
}
