package com.eskcti.algashop.ordering.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecificationTest {

  private final Specification<Object> alwaysTrue = t -> true;
  private final Specification<Object> alwaysFalse = t -> false;

  @Test
  void shouldCombineWithAnd() {
    assertThat(alwaysTrue.and(alwaysTrue).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysTrue.and(alwaysFalse).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.and(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.and(alwaysFalse).isSatisfiedBy(null)).isFalse();
  }

  @Test
  void shouldCombineWithOr() {
    assertThat(alwaysTrue.or(alwaysTrue).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysTrue.or(alwaysFalse).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.or(alwaysTrue).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.or(alwaysFalse).isSatisfiedBy(null)).isFalse();
  }

  @Test
  void shouldNegateWithNot() {
    assertThat(alwaysTrue.not().isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.not().isSatisfiedBy(null)).isTrue();
  }

  @Test
  void shouldCombineWithAndNot() {
    assertThat(alwaysTrue.andNot(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysTrue.andNot(alwaysFalse).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.andNot(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.andNot(alwaysFalse).isSatisfiedBy(null)).isFalse();
  }

  @Test
  void shouldCombineWithOrNot() {
    assertThat(alwaysTrue.orNot(alwaysTrue).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysTrue.orNot(alwaysFalse).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.orNot(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.orNot(alwaysFalse).isSatisfiedBy(null)).isTrue();
  }

  @Test
  void shouldCombineWithNotAnd() {
    assertThat(alwaysTrue.notAnd(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysTrue.notAnd(alwaysFalse).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.notAnd(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysFalse.notAnd(alwaysFalse).isSatisfiedBy(null)).isTrue();
  }

  @Test
  void shouldCombineWithNotOr() {
    assertThat(alwaysTrue.notOr(alwaysTrue).isSatisfiedBy(null)).isFalse();
    assertThat(alwaysTrue.notOr(alwaysFalse).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.notOr(alwaysTrue).isSatisfiedBy(null)).isTrue();
    assertThat(alwaysFalse.notOr(alwaysFalse).isSatisfiedBy(null)).isTrue();
  }

}
