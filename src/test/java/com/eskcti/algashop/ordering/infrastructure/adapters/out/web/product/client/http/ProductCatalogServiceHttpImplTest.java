package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.eskcti.algashop.ordering.core.domain.model.product.Product;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceHttpImplTest {

    @Mock
    private ResilientProductCatalogAPIClient resilientProductCatalogAPIClient;

    @InjectMocks
    private ProductCatalogServiceHttpImpl productCatalogService;

    @Test
    void shouldReturnProductWhenCatalogRespondsSuccessfully() {
        UUID productId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
        ProductResponse response = new ProductResponse(
                productId,
                "Notebook X11",
                new BigDecimal("1000.00"),
                true);

        when(resilientProductCatalogAPIClient.getById(productId))
                .thenReturn(Optional.of(response));

        Optional<Product> product = productCatalogService.ofId(new ProductId(productId));

        assertThat(product).isPresent();
        assertThat(product.get().id().value()).isEqualTo(productId);
        assertThat(product.get().name().value()).isEqualTo("Notebook X11");
        assertThat(product.get().price().value()).isEqualByComparingTo("1000.00");
        assertThat(product.get().inStock()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenCatalogReturnsEmpty() {
        UUID productId = UUID.fromString("21651a12-b126-4213-ac21-19f66ff4642e");

        when(resilientProductCatalogAPIClient.getById(productId))
                .thenReturn(Optional.empty());

        Optional<Product> product = productCatalogService.ofId(new ProductId(productId));

        assertThat(product).isEmpty();
    }
}
