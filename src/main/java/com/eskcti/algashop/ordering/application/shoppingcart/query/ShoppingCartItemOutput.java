package com.eskcti.algashop.ordering.application.shoppingcart.query;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ShoppingCartItemOutput {
  private UUID productId;
  private String name;
  private BigDecimal price;
  private Integer quantity;
  private BigDecimal totalAmount;
  private Boolean available;
}