package com.eskcti.algashop.ordering.infrastructure.product.client.http;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServierHttpImpl implements ProductCatalogService {

    private final ProductCatalogAPIClient productCatalogAPIClient;

    @Override
    public Optional<Product> ofId(ProductId productId) {
        try {
            ProductResponse productResponse = productCatalogAPIClient.getById(productId.value());
            return Optional.of(
                    Product.builder()
                            .id(new ProductId(productResponse.getId()))
                            .name(new ProductName(productResponse.getName()))
                            .inStock(productResponse.getInStock())
                            .price(new Money(productResponse.getSalePrice()))
                            .build());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw e;
        }
    }
}
