package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.order;

import com.eskcti.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.eskcti.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.eskcti.algashop.ordering.core.ports.in.order.OrderFilter;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.CanReadOrders;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final ForQueryingOrders orderQueryService;

  @GetMapping
  @CanReadOrders
  public PageModel<OrderSummaryOutput> findAll(OrderFilter orderFilter) {
    return PageModel.of(orderQueryService.filter(orderFilter));
  }

  @GetMapping("/{orderId}")
  @CanReadOrders
  public OrderDetailOutput findById(@PathVariable String orderId) {
    return orderQueryService.findById(orderId);
  }
}
