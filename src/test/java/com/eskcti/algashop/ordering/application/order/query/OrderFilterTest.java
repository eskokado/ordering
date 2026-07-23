package com.eskcti.algashop.ordering.application.order.query;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class OrderFilterTest {

  @Test
  public void shouldCreateWithAllArgsConstructor() {
    UUID customerId = UUID.randomUUID();

    OrderFilter filter = new OrderFilter(
        "PLACED",
        null,
        customerId,
        null,
        null,
        null,
        null);

    Assertions.assertThat(filter.getStatus()).isEqualTo("PLACED");
    Assertions.assertThat(filter.getCustomerId()).isEqualTo(customerId);
    Assertions.assertThat(filter.getOrderId()).isNull();
    Assertions.assertThat(filter.getPlacedAtFrom()).isNull();
    Assertions.assertThat(filter.getPlacedAtTo()).isNull();
    Assertions.assertThat(filter.getTotalAmountFrom()).isNull();
    Assertions.assertThat(filter.getTotalAmountTo()).isNull();
  }

  @Test
  public void shouldCreateWithSizeAndPage() {
    OrderFilter filter = new OrderFilter(15, 2);

    Assertions.assertThat(filter.getSize()).isEqualTo(15);
    Assertions.assertThat(filter.getPage()).isEqualTo(2);
  }

  @Test
  public void shouldUseDefaultSortWhenNotProvided() {
    OrderFilter filter = new OrderFilter();

    Assertions.assertThat(filter.getSortByPropertyOrDefault())
        .isEqualTo(OrderFilter.SortType.PLACE_AT);
    Assertions.assertThat(filter.getSortDirectionOrDefault())
        .isEqualTo(Sort.Direction.ASC);
  }

  @Test
  public void shouldUseProvidedSortWhenSet() {
    OrderFilter filter = new OrderFilter();
    filter.setSortByProperty(OrderFilter.SortType.STATUS);
    filter.setSortDirection(Sort.Direction.DESC);

    Assertions.assertThat(filter.getSortByPropertyOrDefault())
        .isEqualTo(OrderFilter.SortType.STATUS);
    Assertions.assertThat(filter.getSortDirectionOrDefault())
        .isEqualTo(Sort.Direction.DESC);
  }

}
