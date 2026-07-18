package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;

@DataJpaTest
@Import({
    OrdersPersistenceProvider.class,
    CustomersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    CustomerPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
class OrdersPersistenceProviderIT {

  private OrdersPersistenceProvider persistenceProvider;
  private CustomersPersistenceProvider customersPersistenceProvider;
  private OrderPersistenceEntityRepository entityRepository;

  @Autowired
  public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider,
      CustomersPersistenceProvider customersPersistenceProvider,
      OrderPersistenceEntityRepository entityRepository) {
    this.persistenceProvider = persistenceProvider;
    this.customersPersistenceProvider = customersPersistenceProvider;
    this.entityRepository = entityRepository;
  }

  @Test
  public void shouldUpdateAndKeepPersistenceEntityState() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customersPersistenceProvider.add(customer);

    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .status(OrderStatus.PLACED)
        .build();
    long orderId = order.id().value().toLong();
    persistenceProvider.add(order);

    var persistenceEntity = entityRepository.findById(orderId).orElseThrow();

    Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PLACED.name());

    Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

    order = persistenceProvider.ofId(order.id()).orElseThrow();
    order.markAsPaid();
    persistenceProvider.add(order);

    persistenceEntity = entityRepository.findById(orderId).orElseThrow();

    Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PAID.name());

    Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
    Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void shouldAddFindAndNotFailWhenNoTransaction() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customersPersistenceProvider.add(customer);

    Order order = OrderTestDataBuilder.anOrder()
        .customerId(customer.id())
        .build();
    persistenceProvider.add(order);

    Assertions.assertThatNoException().isThrownBy(
        () -> persistenceProvider.ofId(order.id()).orElseThrow());
  }
}