package com.eskcti.algashop.ordering.infrastructure.persistence.customer;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerPersistenceEntityQueriesImplTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<CustomerOutput> typedQuery;

  @InjectMocks
  private CustomerPersistenceEntityQueriesImpl queries;

  @Test
  void shouldFindCustomerByIdAsOutput() {
    UUID customerId = UUID.randomUUID();
    CustomerOutput customerOutput = CustomerOutput.builder()
        .id(customerId)
        .firstName("John")
        .lastName("Doe")
        .build();

    when(entityManager.createQuery(anyString(), eq(CustomerOutput.class))).thenReturn(typedQuery);
    when(typedQuery.setParameter("id", customerId)).thenReturn(typedQuery);
    when(typedQuery.getSingleResult()).thenReturn(customerOutput);

    Optional<CustomerOutput> result = queries.findByIdAsOutput(customerId);

    assertThat(result).contains(customerOutput);
    verify(typedQuery).setParameter("id", customerId);
  }

  @Test
  void shouldReturnEmptyWhenCustomerDoesNotExist() {
    UUID customerId = UUID.randomUUID();

    when(entityManager.createQuery(anyString(), eq(CustomerOutput.class))).thenReturn(typedQuery);
    when(typedQuery.setParameter("id", customerId)).thenReturn(typedQuery);
    when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

    Optional<CustomerOutput> result = queries.findByIdAsOutput(customerId);

    assertThat(result).isEmpty();
    verify(typedQuery).setParameter("id", customerId);
  }

}
