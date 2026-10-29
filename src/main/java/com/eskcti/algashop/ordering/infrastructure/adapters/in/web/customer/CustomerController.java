package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.eskcti.algashop.ordering.core.ports.in.customer.*;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.PageModel;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ForQueryingCustomers forQueryingCustomers;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    @GetMapping
    @CanReadCustomers
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter) {
        return PageModel.of(forQueryingCustomers.filter(customerFilter));
    }

    @GetMapping("/{customerId}")
    @CanReadCustomers
    public CustomerOutput findById(@PathVariable UUID customerId) {
        return forQueryingCustomers.findById(customerId);
    }

    @GetMapping("/{customerId}/shopping-cart")
    @CanReadShoppingCarts
    public ShoppingCartOutput findShoppingCartByCustomerId(@PathVariable UUID customerId) {
        return forQueryingShoppingCarts.findByCustomerId(customerId);
    }
}
