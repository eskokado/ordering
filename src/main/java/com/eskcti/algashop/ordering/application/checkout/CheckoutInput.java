package com.eskcti.algashop.ordering.application.checkout;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutInput {
  private UUID shoppingCartId;
  private String paymentMethod;
  private ShippingInput shipping;
  private BillingData billing;
}
