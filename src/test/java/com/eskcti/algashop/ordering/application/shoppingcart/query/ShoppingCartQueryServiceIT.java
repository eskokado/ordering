package com.eskcti.algashop.ordering.application.shoppingcart.query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;

@SpringBootTest

@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class ShoppingCartQueryServiceIT {

  @Autowired
  private ShoppingCartQueryService queryService;

  @Autowired
  private ShoppingCarts shoppingCarts;

  @Autowired
  private Customers customers;

  @Test
  public void shouldFindById() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    ShoppingCartOutput output = queryService.findById(shoppingCart.id().value());
    Assertions.assertWith(output,
        o -> Assertions.assertThat(o.getId()).isEqualTo(shoppingCart.id().value()),
        o -> Assertions.assertThat(o.getCustomerId()).isEqualTo(shoppingCart.customerId().value()));
  }

  @Test
  public void shouldFindByCustomerId() {
    Customer customer = CustomerTestDataBuilder.existingCustomer().build();
    customers.add(customer);
    ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
    shoppingCarts.add(shoppingCart);

    ShoppingCartOutput output = queryService.findByCustomerId(customer.id().value());
    Assertions.assertWith(output,
        o -> Assertions.assertThat(o.getId()).isEqualTo(shoppingCart.id().value()),
        o -> Assertions.assertThat(o.getCustomerId()).isEqualTo(shoppingCart.customerId().value()));
  }

}