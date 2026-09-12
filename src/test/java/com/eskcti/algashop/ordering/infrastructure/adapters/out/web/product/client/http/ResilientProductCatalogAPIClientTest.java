package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResilientProductCatalogAPIClientTest {

    @Mock
    private ProductCatalogAPIClient productCatalogAPIClient;

    @InjectMocks
    private ResilientProductCatalogAPIClient resilientClient;

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
    void shouldThrowBadGatewayWhenCatalogRespondsWithOtherClientError() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(badRequest);

        assertThatThrownBy(() -> resilientClient.getById(productId))
                .isInstanceOf(BadGatewayException.class)
                .hasMessage("Product Catalog API Bad Gateway")
                .hasCause(badRequest);
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
}
