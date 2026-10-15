package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.order;

import com.eskcti.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.core.application.checkout.CheckoutApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.eskcti.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.eskcti.algashop.ordering.core.ports.in.order.OrderFilter;
import com.eskcti.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.PageModel;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.UnprocessableEntityException;
import com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.CanReadOrders;
import com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.CanWriteOrders;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final ForQueryingOrders orderQueryService;
  private final CheckoutApplicationService checkoutApplicationService;
  private final BuyNowApplicationService buyNowApplicationService;

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

  @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
  @ResponseStatus(HttpStatus.CREATED)
  @CanWriteOrders
  public OrderDetailOutput createWithProduct(@Valid @RequestBody BuyNowInput input) {
    String orderId;
    try {
      orderId = buyNowApplicationService.buyNow(input);
    } catch (CustomerNotFoundException | ProductNotFoundException e) {
      throw new UnprocessableEntityException(e.getMessage(), e);
    }
    return orderQueryService.findById(orderId);
  }

  @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
  @ResponseStatus(HttpStatus.CREATED)
  @CanWriteOrders
  public OrderDetailOutput createWithShoppingCart(@Valid @RequestBody CheckoutInput input) {
    String orderId;
    try {
      orderId = checkoutApplicationService.checkout(input);
    } catch (CustomerNotFoundException | ShoppingCartNotFoundException e) {
      throw new UnprocessableEntityException(e.getMessage(), e);
    }
    return orderQueryService.findById(orderId);
  }

}
