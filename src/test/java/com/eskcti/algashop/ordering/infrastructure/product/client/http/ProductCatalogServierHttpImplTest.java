package com.eskcti.algashop.ordering.infrastructure.product.client.http;

import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServierHttpImplTest {

    @Mock
    private ProductCatalogAPIClient productCatalogAPIClient;

    @InjectMocks
    private ProductCatalogServierHttpImpl productCatalogService;

    @Test
    void shouldReturnProductWhenCatalogRespondsSuccessfully() {
        UUID productId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
        ProductResponse response = new ProductResponse(
                productId,
                "Notebook X11",
                new BigDecimal("1000.00"),
                true);

        when(productCatalogAPIClient.getById(productId)).thenReturn(response);

        Optional<Product> product = productCatalogService.ofId(new ProductId(productId));

        assertThat(product).isPresent();
        assertThat(product.get().id().value()).isEqualTo(productId);
        assertThat(product.get().name().value()).isEqualTo("Notebook X11");
        assertThat(product.get().price().value()).isEqualByComparingTo("1000.00");
        assertThat(product.get().inStock()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenCatalogRespondsNotFound() {
        UUID productId = UUID.fromString("21651a12-b126-4213-ac21-19f66ff4642e");
        when(productCatalogAPIClient.getById(productId))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        StandardCharsets.UTF_8));

        Optional<Product> product = productCatalogService.ofId(new ProductId(productId));

        assertThat(product).isEmpty();
    }

    @Test
    void shouldRethrowWhenCatalogRespondsWithOtherClientError() {
        UUID productId = UUID.randomUUID();
        HttpClientErrorException badRequest = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                StandardCharsets.UTF_8);

        when(productCatalogAPIClient.getById(productId)).thenThrow(badRequest);

        assertThatThrownBy(() -> productCatalogService.ofId(new ProductId(productId)))
                .isSameAs(badRequest);
    }
}
