package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.application.customer.query.CustomerQueryService;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

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
  public CustomerOutput findById(UUID customerId) {
    try {
      TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
      query.setParameter("id", customerId);
      return query.getSingleResult();
    } catch (NoResultException e) {
      throw new CustomerNotFoundException();
    }
  }
}