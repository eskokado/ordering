package com.eskcti.algashop.ordering.infrastructure.persistence.provider;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;

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