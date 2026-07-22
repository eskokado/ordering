package com.eskcti.algashop.ordering.infrastructure.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eskcti.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.eskcti.algashop.ordering.domain.model.order.Orders;

@Configuration
public class SpecificationBeansConfig {

  @Bean
  public CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(Orders orders) {
    return new CustomerHaveFreeShippingSpecification(
        orders,
        200,
        2L,
        2000);
  }

}