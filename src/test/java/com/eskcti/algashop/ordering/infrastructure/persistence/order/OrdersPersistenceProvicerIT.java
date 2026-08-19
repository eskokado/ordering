package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.core.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.order.Order;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;

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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
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