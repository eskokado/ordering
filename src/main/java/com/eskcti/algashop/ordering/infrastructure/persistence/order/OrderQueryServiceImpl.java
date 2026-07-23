package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.eskcti.algashop.ordering.application.order.query.OrderDetailOutput;
import com.eskcti.algashop.ordering.application.order.query.OrderFilter;
import com.eskcti.algashop.ordering.application.order.query.OrderQueryService;
import com.eskcti.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

  private final OrderPersistenceEntityRepository repository;
  private final Mapper mapper;

  private final EntityManager entityManager;

  @Override
  public OrderDetailOutput findById(String id) {
    OrderPersistenceEntity entity = repository.findById(new OrderId(id).value().toLong())
        .orElseThrow(() -> new OrderNotFoundException());
    return mapper.convert(entity, OrderDetailOutput.class);
  }

  @Override
  public Page<OrderSummaryOutput> filter(OrderFilter filter) {
    Long totalQueryResults = countTotalQueryResults(filter);

    if (totalQueryResults.equals(0L)) {
      PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
      return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
    }

    return filterQuery(filter, totalQueryResults);
  }

  private Long countTotalQueryResults(OrderFilter filter) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
    Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);

    Expression<Long> count = builder.count(root);
    Predicate[] predicates = toPredicates(builder, root, filter);

    criteriaQuery.select(count);
    criteriaQuery.where(predicates);

    TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

    return query.getSingleResult();
  }

  private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OrderPersistenceEntity> criteriaQuery = builder.createQuery(OrderPersistenceEntity.class);

    Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);
    root.fetch("customer", JoinType.INNER);

    Predicate[] predicates = toPredicates(builder, root, filter);

    criteriaQuery.select(root).distinct(true);
    criteriaQuery.where(predicates);
    criteriaQuery.orderBy(toSortOrder(builder, root, filter));

    TypedQuery<OrderPersistenceEntity> typedQuery = entityManager.createQuery(criteriaQuery);

    typedQuery.setFirstResult(filter.getSize() * filter.getPage());
    typedQuery.setMaxResults(filter.getSize());

    List<OrderSummaryOutput> content = typedQuery.getResultList().stream()
        .map(this::toSummaryOutput)
        .toList();

    PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

    return new PageImpl<>(content, pageRequest, totalQueryResults);
  }

  private OrderSummaryOutput toSummaryOutput(OrderPersistenceEntity entity) {
    CustomerPersistenceEntity customer = entity.getCustomer();

    CustomerMinimalOutput customerOutput = new CustomerMinimalOutput(
        customer.getId(),
        customer.getFirstName(),
        customer.getLastName(),
        customer.getEmail(),
        customer.getDocument(),
        customer.getPhone());

    return new OrderSummaryOutput(
        entity.getId(),
        customerOutput,
        entity.getTotalItems(),
        entity.getTotalAmount(),
        entity.getPlacedAt(),
        entity.getPaidAt(),
        entity.getCanceledAt(),
        entity.getReadyAt(),
        entity.getStatus(),
        entity.getPaymentMethod());
  }

  private jakarta.persistence.criteria.Order toSortOrder(CriteriaBuilder builder, Root<OrderPersistenceEntity> root,
      OrderFilter filter) {
    Path<?> sortPath = root.get(filter.getSortByPropertyOrDefault().getPropertyName());

    if (Sort.Direction.ASC.equals(filter.getSortDirectionOrDefault())) {
      return builder.asc(sortPath);
    }

    return builder.desc(sortPath);
  }

  private Predicate[] toPredicates(CriteriaBuilder builder,
      Root<OrderPersistenceEntity> root, OrderFilter filter) {
    List<Predicate> predicates = new ArrayList<>();

    if (filter.getCustomerId() != null) {
      predicates.add(builder.equal(root.get("customer").get("id"), filter.getCustomerId()));
    }

    if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
      predicates.add(builder.equal(root.get("status"), filter.getStatus().toUpperCase()));
    }

    if (filter.getOrderId() != null) {
      long orderIdLongValue;
      try {
        OrderId orderId = new OrderId(filter.getOrderId());
        orderIdLongValue = orderId.value().toLong();
      } catch (IllegalArgumentException e) {
        orderIdLongValue = 0L;
      }
      predicates.add(builder.equal(root.get("id"), orderIdLongValue));
    }

    if (filter.getPlacedAtFrom() != null) {
      predicates.add(builder.greaterThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtFrom()));
    }

    if (filter.getPlacedAtTo() != null) {
      predicates.add(builder.lessThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtTo()));
    }

    if (filter.getTotalAmountFrom() != null) {
      predicates.add(builder.greaterThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountFrom()));
    }

    if (filter.getTotalAmountTo() != null) {
      predicates.add(builder.lessThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountTo()));
    }

    return predicates.toArray(new Predicate[] {});

  }
}