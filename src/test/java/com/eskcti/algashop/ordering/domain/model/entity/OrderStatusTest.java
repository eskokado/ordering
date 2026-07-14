package com.eskcti.algashop.ordering.domain.model.entity;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

  @Test
  void given_draft_whenChangeToPlacedOrCanceled_shouldBeAllowed() {
    assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.PLACED)).isTrue();
    assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.CANCELED)).isTrue();
    assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.PAID)).isTrue();
    assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.READY)).isTrue();
  }

  @Test
  void given_placed_whenChangeToPaidOrCanceled_shouldBeAllowed() {
    assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.PAID)).isTrue();
    assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.CANCELED)).isTrue();
    assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.READY)).isTrue();
  }

  @Test
  void given_paid_whenChangeToReadyOrCanceled_shouldBeAllowed() {
    assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.READY)).isTrue();
    assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.CANCELED)).isTrue();
    assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.PLACED)).isTrue();
  }

  @Test
  void given_ready_whenChangeToCanceled_shouldBeAllowed() {
    assertThat(OrderStatus.READY.canChangeTo(OrderStatus.CANCELED)).isTrue();
    assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.PLACED)).isTrue();
    assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.PAID)).isTrue();
  }

  @Test
  void given_canceled_whenChangeToAnyStatus_shouldNotBeAllowed() {
    assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.PLACED)).isTrue();
    assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.PAID)).isTrue();
    assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.READY)).isTrue();
    assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.CANCELED)).isTrue();
  }

  @Test
  void given_sameStatus_whenCheckTransition_shouldNotBeAllowed() {
    assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.PLACED)).isTrue();
    assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.PAID)).isTrue();
    assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.READY)).isTrue();
  }
}
