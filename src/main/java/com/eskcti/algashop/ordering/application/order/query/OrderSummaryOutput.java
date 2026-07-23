package com.eskcti.algashop.ordering.application.order.query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.eskcti.algashop.ordering.domain.model.order.OrderId;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSummaryOutput {
  private String id;
  private CustomerMinimalOutput customer;
  private Integer totalItems;
  private BigDecimal totalAmount;
  private OffsetDateTime placedAt;
  private OffsetDateTime paidAt;
  private OffsetDateTime canceledAt;
  private OffsetDateTime readyAt;
  private String status;
  private String paymentMethod;

  public OrderSummaryOutput(Long id, CustomerMinimalOutput customer, Integer totalItems,
      BigDecimal totalAmount, OffsetDateTime placedAt, OffsetDateTime paidAt,
      OffsetDateTime canceledAt, OffsetDateTime readyAt,
      String status, String paymentMethod) {
    this.id = new OrderId(id).toString();
    this.customer = customer;
    this.totalItems = totalItems;
    this.totalAmount = totalAmount;
    this.placedAt = placedAt;
    this.paidAt = paidAt;
    this.canceledAt = canceledAt;
    this.readyAt = readyAt;
    this.status = status;
    this.paymentMethod = paymentMethod;
  }
}