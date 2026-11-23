package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import com.eskcti.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.eskcti.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig.rapidexAPICBId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResilientRapiDexAPIClientTest {

    @Mock
    private RapiDexAPIClient rapiDexAPIClient;

    private FrameworkRetryCircuitBreakerFactory circuitBreakerFactory;
    private ResilientRapiDexAPIClient resilientClient;

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
                .openTimeout(Duration.ofMillis(500))
                .resetTimeout(Duration.ofSeconds(10))
                .build(), SpringCircuitBreakerConfig.rapidexAPICBId);

        resilientClient = new ResilientRapiDexAPIClient(circuitBreakerFactory, rapiDexAPIClient, "20.0", 10L);
    }

    @Test
    void shouldReturnDeliveryCostWhenRapidexRespondsSuccessfully() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        DeliveryCostResponse response = new DeliveryCostResponse("10.00", 5L);

        when(rapiDexAPIClient.calculate(request)).thenReturn(response);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("10.00");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(5L);
    }

    @Test
    void shouldThrowClientErrorWhenNotFound() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(notFound);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");
    }

    @Test
    void shouldThrowClientErrorWhenClientReturnsNull() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");

        when(rapiDexAPIClient.calculate(request)).thenReturn(null);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");
    }

    @Test
    void shouldReturnFallbackWhenUnreachable() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(rapiDexAPIClient.calculate(request)).thenThrow(timeout);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
    }

    @Test
    void shouldReturnFallbackOnSocketTimeout() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        SocketTimeoutException socketTimeout = new SocketTimeoutException("Read timed out");
        RestClientException restClientException = new RestClientException("Timeout", socketTimeout);

        when(rapiDexAPIClient.calculate(request)).thenThrow(restClientException);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
    }

    @Test
    void shouldThrowClientErrorOnBadRequest() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(badRequest);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");
    }

    @Test
    void shouldThrowClientErrorOnConflict() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpClientErrorException conflict = HttpClientErrorException.create(
                HttpStatus.CONFLICT,
                "Conflict",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(conflict);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");
    }

    @Test
    void shouldReturnFallbackOnGenericRestClientError() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        RestClientException restClientException = new RestClientException("error",
                new RuntimeException("connection refused"));

        when(rapiDexAPIClient.calculate(request)).thenThrow(restClientException);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
    }

    @Test
    void shouldReturnFallbackOnServerError() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(serverError);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
    }

    @Test
    void shouldRetryOnTimeoutAndReturnFallback() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(rapiDexAPIClient.calculate(request)).thenThrow(timeout);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
        verify(rapiDexAPIClient, times(4)).calculate(request);
    }

    @Test
    void shouldRetryOnServerErrorAndReturnFallback() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(serverError);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
        verify(rapiDexAPIClient, times(4)).calculate(request);
    }

    @Test
    void shouldNotRetryOnClientErrorAndThrowException() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(badRequest);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");

        verify(rapiDexAPIClient, times(1)).calculate(request);
    }

    @Test
    void shouldNotRetryOnGenericErrorAndReturnFallback() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        RestClientException restClientException = new RestClientException("error",
                new RuntimeException("connection refused"));

        when(rapiDexAPIClient.calculate(request)).thenThrow(restClientException);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);

        verify(rapiDexAPIClient, times(1)).calculate(request);
    }

    @Test
    void shouldSucceedAfterRetryRecovery() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        DeliveryCostResponse response = new DeliveryCostResponse("15.00", 3L);

        when(rapiDexAPIClient.calculate(request))
                .thenThrow(new ResourceAccessException("Connection timed out"))
                .thenReturn(response);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("15.00");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(3L);
        verify(rapiDexAPIClient, times(2)).calculate(request);
    }

    @Test
    void shouldOpenCircuitAfterFirstExhaustedFailureAndReturnFallback() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(rapiDexAPIClient.calculate(request)).thenThrow(timeout);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
        assertThat(circuitBreakerPolicy().getState()).isEqualTo(CircuitBreakerRetryPolicy.State.OPEN);
        verify(rapiDexAPIClient, times(4)).calculate(request);
    }

    @Test
    void shouldFailFastWithFallbackWithoutCallingDelegateWhenCircuitIsOpen() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(rapiDexAPIClient.calculate(request)).thenThrow(timeout);

        resilientClient.calculate(request);
        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(circuitBreakerPolicy().getState()).isEqualTo(CircuitBreakerRetryPolicy.State.OPEN);
        verify(rapiDexAPIClient, times(4)).calculate(request);
    }

    @Test
    void shouldTransitionToHalfOpenAndCloseCircuitAfterOpenTimeout() throws InterruptedException {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");
        DeliveryCostResponse response = new DeliveryCostResponse("15.00", 3L);

        when(rapiDexAPIClient.calculate(request))
                .thenThrow(timeout)
                .thenThrow(timeout)
                .thenThrow(timeout)
                .thenThrow(timeout)
                .thenReturn(response);

        DeliveryCostResponse fallback = resilientClient.calculate(request);
        assertThat(fallback.getDeliveryCost()).isEqualTo("20.0");
        assertThat(circuitBreakerPolicy().getState()).isEqualTo(CircuitBreakerRetryPolicy.State.OPEN);

        Thread.sleep(700);

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result.getDeliveryCost()).isEqualTo("15.00");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(3L);
        assertThat(circuitBreakerPolicy().getState()).isEqualTo(CircuitBreakerRetryPolicy.State.CLOSED);
        verify(rapiDexAPIClient, times(5)).calculate(request);
    }

    @Test
    void shouldReopenCircuitWhenHalfOpenProbeFails() throws InterruptedException {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        ResourceAccessException timeout = new ResourceAccessException("Connection timed out");

        when(rapiDexAPIClient.calculate(request)).thenThrow(timeout);

        resilientClient.calculate(request);

        Thread.sleep(700);

        resilientClient.calculate(request);
        assertThat(circuitBreakerPolicy().getState()).isEqualTo(CircuitBreakerRetryPolicy.State.OPEN);

        resilientClient.calculate(request);
        verify(rapiDexAPIClient, times(8)).calculate(request);
    }

    @Test
    void shouldNotRetryWhenMaxRetriesIsConfiguredToZero() {
        FrameworkRetryCircuitBreakerFactory factory =
                factoryWith(0, Duration.ofSeconds(10), Duration.ofSeconds(25));
        ResilientRapiDexAPIClient client =
                new ResilientRapiDexAPIClient(factory, rapiDexAPIClient, "20.0", 10L);

        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        when(rapiDexAPIClient.calculate(request))
                .thenThrow(new ResourceAccessException("Connection timed out"));

        DeliveryCostResponse result = client.calculate(request);

        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(policyOf(factory).getState()).isEqualTo(CircuitBreakerRetryPolicy.State.OPEN);
        verify(rapiDexAPIClient, times(1)).calculate(request);
    }

    @Test
    void shouldReturnFallbackOnUnexpectedException() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        when(rapiDexAPIClient.calculate(request))
                .thenThrow(new RuntimeException("unexpected"));

        DeliveryCostResponse result = resilientClient.calculate(request);

        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo("20.0");
        assertThat(result.getEstimatedDaysToDeliver()).isEqualTo(10L);
    }

    @Test
    void shouldThrowClientErrorOnNotFoundWithoutLoggingWarning() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                StandardCharsets.UTF_8);

        when(rapiDexAPIClient.calculate(request)).thenThrow(notFound);

        assertThatThrownBy(() -> resilientClient.calculate(request))
                .isInstanceOf(BadGatewayException.ClientErrorException.class)
                .hasMessage("Invalid zip code provided");
    }

    @Test
    void shouldCallDoCalculateExactlyOnceOnSuccess() {
        DeliveryCostRequest request = new DeliveryCostRequest("12345", "54321");
        DeliveryCostResponse response = new DeliveryCostResponse("10.00", 5L);

        when(rapiDexAPIClient.calculate(request)).thenReturn(response);

        resilientClient.calculate(request);

        verify(rapiDexAPIClient, times(1)).calculate(request);
    }

    private CircuitBreakerRetryPolicy circuitBreakerPolicy() {
        return policyOf(circuitBreakerFactory);
    }

    private static CircuitBreakerRetryPolicy policyOf(FrameworkRetryCircuitBreakerFactory factory) {
        FrameworkRetryCircuitBreaker circuitBreaker =
                (FrameworkRetryCircuitBreaker) factory.create(rapidexAPICBId);
        return circuitBreaker.getCircuitBreakerPolicy();
    }

    private static FrameworkRetryCircuitBreakerFactory factoryWith(
            long maxRetries, Duration openTimeout, Duration resetTimeout) {
        FrameworkRetryCircuitBreakerFactory factory = new FrameworkRetryCircuitBreakerFactory();
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(maxRetries)
                .multiplier(2)
                .delay(Duration.ofMillis(10))
                .includes(GatewayTimeoutException.class, BadGatewayException.ServerErrorException.class)
                .build();
        factory.configure(builder -> builder
                .retryPolicy(retryPolicy)
                .openTimeout(openTimeout)
                .resetTimeout(resetTimeout)
                .build(), rapidexAPICBId);
        return factory;
    }
}
