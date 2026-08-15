package com.eskcti.algashop.ordering.application.shoppingcart.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartOutput {
  private UUID id;
  private UUID customerId;
  private Integer totalItems;
  private BigDecimal totalAmount;

  @Builder.Default
  private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
