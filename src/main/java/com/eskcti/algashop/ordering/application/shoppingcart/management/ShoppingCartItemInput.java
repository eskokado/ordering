package com.eskcti.algashop.ordering.application.shoppingcart.management;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartItemInput {
  private Integer quantity;
  private UUID productId;
  private UUID shoppingCartId;
}
