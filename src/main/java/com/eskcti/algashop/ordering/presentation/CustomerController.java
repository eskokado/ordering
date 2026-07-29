package com.eskcti.algashop.ordering.presentation;

import com.eskcti.algashop.ordering.application.customer.management.CustomerInput;
import com.eskcti.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.eskcti.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.eskcti.algashop.ordering.application.customer.query.CustomerFilter;
import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.application.customer.query.CustomerQueryService;
import com.eskcti.algashop.ordering.application.customer.query.CustomerSummaryOutput;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService customerQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input, HttpServletResponse httpServletResponse) {
        UUID customerId = customerManagementApplicationService.create(input);

        UriComponentsBuilder builder = fromMethodCall(on(CustomerController.class).findById(customerId));
        httpServletResponse.addHeader("Location", builder.toUriString());

        return customerQueryService.findById(customerId);
    }

    @GetMapping
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter) {
        return PageModel.of(customerQueryService.filter(customerFilter));
    }

    @GetMapping("/{customerId}")
    public CustomerOutput findById(@PathVariable UUID customerId) {
        return customerQueryService.findById(customerId);
    }

    @PutMapping("/{customerId}")
    public CustomerOutput update(@PathVariable UUID customerId,
            @RequestBody @Valid CustomerUpdateInput input) {
        customerManagementApplicationService.update(customerId, input);
        return customerQueryService.findById(customerId);
    }

}
