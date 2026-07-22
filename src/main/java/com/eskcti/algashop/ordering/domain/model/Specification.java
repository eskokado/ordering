package com.eskcti.algashop.ordering.domain.model;

public interface Specification<T> {
  boolean isSatisfiedBy(T t);

  default Specification<T> and(Specification<T> other) {
    return t -> isSatisfiedBy(t) && other.isSatisfiedBy(t);
  }

  default Specification<T> or(Specification<T> other) {
    return t -> isSatisfiedBy(t) || other.isSatisfiedBy(t);
  }

  default Specification<T> not() {
    return t -> !isSatisfiedBy(t);
  }

  default Specification<T> andNot(Specification<T> other) {
    return t -> isSatisfiedBy(t) && !other.isSatisfiedBy(t);
  }

  default Specification<T> orNot(Specification<T> other) {
    return t -> isSatisfiedBy(t) || !other.isSatisfiedBy(t);
  }

  default Specification<T> notAnd(Specification<T> other) {
    return t -> !isSatisfiedBy(t) && !other.isSatisfiedBy(t);
  }

  default Specification<T> notOr(Specification<T> other) {
    return t -> !isSatisfiedBy(t) || !other.isSatisfiedBy(t);
  }
}
