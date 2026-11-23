package com.eskcti.algashop.ordering.infrastructure.config.resilience;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;

import static com.eskcti.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig.productCatalogCBId;
import static com.eskcti.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig.rapidexAPICBId;
import static org.assertj.core.api.Assertions.assertThat;

class SpringCircuitBreakerConfigTest {

    private FrameworkRetryCircuitBreakerFactory circuitBreakerFactory;

    @BeforeEach
    void setUp() {
        circuitBreakerFactory = new FrameworkRetryCircuitBreakerFactory();
        new SpringCircuitBreakerConfig().defaultCustomizer().customize(circuitBreakerFactory);
    }

    @ParameterizedTest
    @ValueSource(strings = {productCatalogCBId, rapidexAPICBId})
    void shouldConfigureOpenAndResetTimeouts(String circuitBreakerId) {
        CircuitBreakerRetryPolicy policy = policyFor(circuitBreakerId);

        assertThat(policy.getOpenTimeout()).isEqualTo(Duration.ofSeconds(10));
        assertThat(policy.getResetTimeout()).isEqualTo(Duration.ofSeconds(25));
    }

    @ParameterizedTest
    @ValueSource(strings = {productCatalogCBId, rapidexAPICBId})
    void shouldConfigureRetryPolicyWithExponentialBackoff(String circuitBreakerId) {
        RetryPolicy retryPolicy = policyFor(circuitBreakerId).getRetryPolicy();

        assertThat(retryPolicy.getBackOff()).isInstanceOf(ExponentialBackOff.class);

        ExponentialBackOff backOff = (ExponentialBackOff) retryPolicy.getBackOff();
        assertThat(backOff.getMaxAttempts()).isEqualTo(3);
        assertThat(backOff.getInitialInterval()).isEqualTo(Duration.ofSeconds(3).toMillis());
        assertThat(backOff.getMultiplier()).isEqualTo(2.0);
    }

    @ParameterizedTest
    @ValueSource(strings = {productCatalogCBId, rapidexAPICBId})
    void shouldRetryOnlyTransientFailures(String circuitBreakerId) {
        RetryPolicy retryPolicy = policyFor(circuitBreakerId).getRetryPolicy();

        assertThat(retryPolicy.shouldRetry(new GatewayTimeoutException("timeout"))).isTrue();
        assertThat(retryPolicy.shouldRetry(new BadGatewayException.ServerErrorException())).isTrue();
        assertThat(retryPolicy.shouldRetry(new BadGatewayException.ClientErrorException("client error"))).isFalse();
        assertThat(retryPolicy.shouldRetry(new RuntimeException("unexpected"))).isFalse();
    }

    @Test
    void shouldStartCircuitClosedForBothCircuitBreakers() {
        assertThat(policyFor(productCatalogCBId).getState())
                .isEqualTo(CircuitBreakerRetryPolicy.State.CLOSED);
        assertThat(policyFor(rapidexAPICBId).getState())
                .isEqualTo(CircuitBreakerRetryPolicy.State.CLOSED);
    }

    private CircuitBreakerRetryPolicy policyFor(String circuitBreakerId) {
        FrameworkRetryCircuitBreaker circuitBreaker =
                (FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(circuitBreakerId);
        return circuitBreaker.getCircuitBreakerPolicy();
    }
}
