package com.eskcti.algashop.ordering.core.application.shoppingcart.query;

import com.eskcti.algashop.ordering.core.application.AbstractApplicationIT;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartQueryService;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;


class ShoppingCartQueryServiceIT extends AbstractApplicationIT {

  @Autowired
  private ForQueryingShoppingCarts forQueryingShoppingCarts;

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

    ShoppingCartOutput output = forQueryingShoppingCarts.findById(shoppingCart.id().value());
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

    ShoppingCartOutput output = forQueryingShoppingCarts.findByCustomerId(customer.id().value());
    Assertions.assertWith(output,
        o -> Assertions.assertThat(o.getId()).isEqualTo(shoppingCart.id().value()),
        o -> Assertions.assertThat(o.getCustomerId()).isEqualTo(shoppingCart.customerId().value()));
  }

}
