package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;

import com.eskcti.algashop.ordering.utils.SecurityCheckTestConfig;
import com.eskcti.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eskcti.algashop.ordering.core.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.order.Order;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order.OrdersPersistenceProvider;
import org.springframework.context.annotation.Import;

@Import({
    OrdersPersistenceProvider.class,
    CustomersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    CustomerPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class,
    TestcontainerPostgreSQLConfig.class,
    SecurityCheckTestConfig.class
})
class OrdersPersistenceProviderIT extends AbstractPersistenceIT {

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
