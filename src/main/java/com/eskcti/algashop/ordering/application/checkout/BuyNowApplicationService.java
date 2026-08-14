package com.eskcti.algashop.ordering.application.checkout;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.DomainException;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.order.Billing;
import com.eskcti.algashop.ordering.domain.model.order.BuyNowService;
import com.eskcti.algashop.ordering.domain.model.order.CreditCardId;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.Orders;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.order.Shipping;
import com.eskcti.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

  private final BuyNowService buyNowService;
  private final ProductCatalogService productCatalogService;

  private final ShippingCostService shippingCostService;
  private final OriginAddressService originAddressService;

  private final Orders orders;
  private final Customers customers;

  private final ShippingInputDisassembler shippingInputDisassembler;
  private final BillingInputDisassembler billingInputDisassembler;

  @Transactional
  public String buyNow(BuyNowInput input) {
    Objects.requireNonNull(input);

    PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
    CustomerId customerId = new CustomerId(input.getCustomerId());
    Quantity quantity = new Quantity(input.getQuantity());

    Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

    ProductId productId = new ProductId(input.getProductId());
    CreditCardId creditCardId = null;

    if (paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
      if (input.getCreditCardId() == null) {
        throw new DomainException("Credit card id is required");
      }
      creditCardId = new CreditCardId(input.getCreditCardId());
    }
    Product product = productCatalogService.ofId(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    var shippingCalculationResult = calculateShippingCost(input.getShipping());

    Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(),
        shippingCalculationResult);

    Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());

    Order order = buyNowService.buyNow(
        product, customer, billing, shipping, quantity, paymentMethod, creditCardId);

    orders.add(order);

    return order.id().toString();
  }

  private ShippingCostService.CalculationResult calculateShippingCost(ShippingInput shipping) {
    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());
    return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
  }
}