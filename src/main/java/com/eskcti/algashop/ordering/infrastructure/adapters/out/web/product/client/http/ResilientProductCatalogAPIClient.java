package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import static com.eskcti.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig.productCatalogCBId;

import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class ResilientProductCatalogAPIClient {

    private static final int MAX_CAUSE_DEPTH = 10;

    private final ProductCatalogAPIClient productCatalogAPIClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public ResilientProductCatalogAPIClient(CircuitBreakerFactory circuitBreakerFactory,
                                            ProductCatalogAPIClient productCatalogAPIClient) {
        this.productCatalogAPIClient = productCatalogAPIClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Cacheable(
        cacheNames = "algashop:product-catalog-api:v1", 
        key = "#productId",
        unless="#result == null"
    )
    @ConcurrencyLimit(10)
    public Optional<ProductResponse> getById(UUID productId) {
        log.info("Trying to load product {}", productId);
        try {
            return circuitBreakerFactory.create(productCatalogCBId).run(()->loadProduct(productId));
        } catch (NoFallbackAvailableException e) {
            if (e.getCause() instanceof RetryException re) {
                if (re.getCause() instanceof GatewayTimeoutException gte) {
                    throw gte;
                }
                if (re.getCause() instanceof BadGatewayException bge) {
                    throw bge;
                }
            }
            throw e;
        }
    }

    private Optional<ProductResponse> loadProduct(UUID productId) {
        log.info("Loading product {}", productId);
        try {
            return Optional.ofNullable(productCatalogAPIClient.getById(productId));
        } catch (OAuth2AuthorizationException e) {
            throw translateTokenException(e);
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw translateException(e);
        }
    }

    private RuntimeException translateTokenException(OAuth2AuthorizationException e) {
        RestClientException cause = findRestClientCause(e);

        if (cause == null) {
            return new BadGatewayException("Product Catalog API Bad Gateway", e);
        }

        if (cause instanceof ResourceAccessException
            || cause.getCause() instanceof SocketTimeoutException) {
            return new GatewayTimeoutException("Product Catalog API Timeout", e);
        }

        if (cause instanceof HttpClientErrorException) {
            return new BadGatewayException.ClientErrorException("Product Catalog API Bad Gateway", e);
        }

        if (cause instanceof HttpServerErrorException) {
            return new BadGatewayException.ServerErrorException("Product Catalog API Bad Gateway", e);
        }

        return new BadGatewayException("Product Catalog API Bad Gateway", e);
    }

    private RestClientException findRestClientCause(Throwable exception) {
        Throwable current = exception;
        int depth = 0;

        while (current != null && depth++ < MAX_CAUSE_DEPTH) {
            if (current instanceof RestClientException restClientException) {
                return restClientException;
            }
            current = current.getCause();
        }

        return null;
    }

    private RuntimeException translateException(RestClientException e) {
        if (e.getCause() instanceof SocketTimeoutException
            || e instanceof ResourceAccessException) {
            return new GatewayTimeoutException("Product Catalog API Timeout", e);
        }

        if (e instanceof HttpClientErrorException) {
            return new BadGatewayException.ClientErrorException("Product Catalog API Bad Gateway", e);
        }

        if (e instanceof HttpServerErrorException) {
            return new BadGatewayException.ServerErrorException("Product Catalog API Bad Gateway", e);
        }

        return new BadGatewayException("Product Catalog API Bad Gateway", e);
    }

}