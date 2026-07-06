package com.eskcti.algashop.ordering.domain.valueobject.id;

import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class OrderItemIdTest {

  @Test
  void shouldCreateFromTsid() {
    TSID tsid = TSID.from(98765L);
    OrderItemId orderItemId = new OrderItemId(tsid);

    Assertions.assertThat(orderItemId.value()).isEqualTo(tsid);
    Assertions.assertThat(orderItemId.toString()).isEqualTo(tsid.toString());
  }

  @Test
  void shouldCreateFromLong() {
    OrderItemId orderItemId = new OrderItemId(98765L);

    Assertions.assertThat(orderItemId.value()).isEqualTo(TSID.from(98765L));
  }

  @Test
  void shouldCreateFromString() {
    String tsidValue = TSID.from(98765L).toString();
    OrderItemId orderItemId = new OrderItemId(tsidValue);

    Assertions.assertThat(orderItemId.value()).isEqualTo(TSID.from(tsidValue));
  }

  @Test
  void shouldNotCreateWithNullTsid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new OrderItemId((TSID) null));
  }
}
