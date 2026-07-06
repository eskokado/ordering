package com.eskcti.algashop.ordering.domain.valueobject.id;

import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class OrderIdTest {

  @Test
  void shouldCreateFromTsid() {
    TSID tsid = TSID.from(12345L);
    OrderId orderId = new OrderId(tsid);

    Assertions.assertThat(orderId.value()).isEqualTo(tsid);
    Assertions.assertThat(orderId.toString()).isEqualTo(tsid.toString());
  }

  @Test
  void shouldCreateFromLong() {
    OrderId orderId = new OrderId(12345L);

    Assertions.assertThat(orderId.value()).isEqualTo(TSID.from(12345L));
  }

  @Test
  void shouldCreateFromString() {
    String tsidValue = TSID.from(12345L).toString();
    OrderId orderId = new OrderId(tsidValue);

    Assertions.assertThat(orderId.value()).isEqualTo(TSID.from(tsidValue));
  }

  @Test
  void shouldNotCreateWithNullTsid() {
    Assertions.assertThatNullPointerException()
        .isThrownBy(() -> new OrderId((TSID) null));
  }
}
