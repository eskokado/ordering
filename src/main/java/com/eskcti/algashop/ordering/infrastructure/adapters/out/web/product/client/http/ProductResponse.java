package com.eskcti.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse implements Serializable {
    private UUID id;
    private String name;
    private BigDecimal salePrice;
    private Boolean inStock;
}
