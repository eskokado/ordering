package com.eskcti.algashop.ordering.infrastructure.persistence.entity;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = "id")
@Table(name = "\"order\"")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderPersistenceEntity {
  @Id
  @EqualsAndHashCode.Include
  private Long id;
  private UUID customerId;

  private BigDecimal totalAmount;
  private Integer totalItems;
  private String status;
  private String paymentMethod;

  private OffsetDateTime placedAt;
  private OffsetDateTime paidAt;
  private OffsetDateTime canceledAt;
  private OffsetDateTime readyAt;

}
