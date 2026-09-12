package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.product.Product;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductName;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {

    private final ResilientProductCatalogAPIClient productCatalogAPIClient;

    @Override
    public Optional<Product> ofId(ProductId productId) {
        return productCatalogAPIClient.getById(productId.value())
                .map(productResponse ->
                    Product.builder()
                        .id(new ProductId(productResponse.getId()))
                        .name(new ProductName(productResponse.getName()))
                        .inStock(productResponse.getInStock())
                        .price(new Money(productResponse.getSalePrice()))
                        .build()
        );
    }
}
