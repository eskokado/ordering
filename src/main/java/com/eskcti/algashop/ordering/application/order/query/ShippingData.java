package com.eskcti.algashop.ordering.application.order.query;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.eskcti.algashop.ordering.application.checkout.RecipientData;
import com.eskcti.algashop.ordering.application.commons.AddressData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingData {
  private BigDecimal cost;
  private LocalDate expectedDate;
  private RecipientData recipient;
  private AddressData address;
}