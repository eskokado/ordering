package com.eskcti.algashop.ordering.core.application.commons;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressData {
  @NotBlank
  private String street;

  @NotBlank
  private String number;

  private String complement;

  @NotBlank
  private String neighborhood;

  @NotBlank
  private String city;

  @NotBlank
  private String state;

  @NotBlank
  @Size(min = 5, max = 5, message = "Zip code must have exactly 5 digits")
  private String zipCode;
}
