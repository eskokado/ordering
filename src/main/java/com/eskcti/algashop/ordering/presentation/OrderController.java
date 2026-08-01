package com.eskcti.algashop.ordering.presentation;

import com.eskcti.algashop.ordering.application.order.query.OrderDetailOutput;
import com.eskcti.algashop.ordering.application.order.query.OrderFilter;
import com.eskcti.algashop.ordering.application.order.query.OrderQueryService;
import com.eskcti.algashop.ordering.application.order.query.OrderSummaryOutput;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderQueryService orderQueryService;

  @GetMapping
  public PageModel<OrderSummaryOutput> findAll(OrderFilter orderFilter) {
    return PageModel.of(orderQueryService.filter(orderFilter));
  }

  @GetMapping("/{orderId}")
  public OrderDetailOutput findById(@PathVariable String orderId) {
    return orderQueryService.findById(orderId);
  }

}
