package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import java.util.Optional;
import java.util.UUID;

import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerPersistenceEntityQueriesImpl implements CustomerPersistenceEntityQueries {

  private final EntityManager entityManager;

  private static final String findByIdAsOutputJPQL = """
      SELECT new com.eskcti.algashop.ordering.application.customer.query.CustomerOutput(
          c.id,
          c.firstName,
          c.lastName,
          c.email,
          c.document,
          c.phone,
          c.birthDate,
          c.loyaltyPoints,
          c.registeredAt,
          c.archivedAt,
          c.promotionNotificationsAllowed,
          c.archived,
          new com.eskcti.algashop.ordering.application.commons.AddressData(
              c.address.street,
              c.address.number,
              c.address.complement,
              c.address.neighborhood,
              c.address.city,
              c.address.state,
              c.address.zipCode
          )
      )
      FROM CustomerPersistenceEntity c
      WHERE c.id = :id""";

  @Override
  public Optional<CustomerOutput> findByIdAsOutput(UUID customerId) {
    try {
      TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
      query.setParameter("id", customerId);
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}