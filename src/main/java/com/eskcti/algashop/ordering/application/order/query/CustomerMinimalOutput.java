package com.eskcti.algashop.ordering.application.order.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerMinimalOutput {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String document;
  private String phone;
}