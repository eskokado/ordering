package com.eskcti.algashop.ordering.application.checkout;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.order.Billing;
import com.eskcti.algashop.ordering.domain.model.order.BuyNowService;
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

  private final ShippingInputDisassembler shippingInputDisassembler;
  private final BillingInputDisassembler billingInputDisassembler;

  @Transactional
  public String buyNow(BuyNowInput input) {
    Objects.requireNonNull(input);

    PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
    CustomerId customerId = new CustomerId(input.getCustomerId());
    Quantity quantity = new Quantity(input.getQuantity());

    Product product = findProduct(new ProductId(input.getProductId()));

    var shippingCalculationResult = calculateShippingCost(input.getShipping());

    Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(),
        shippingCalculationResult);

    Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());

    Order order = buyNowService.buyNow(
        product, customerId, billing, shipping, quantity, paymentMethod);

    orders.add(order);

    return order.id().toString();
  }

  private ShippingCostService.CalculationResult calculateShippingCost(ShippingInput shipping) {
    ZipCode origin = originAddressService.originAddress().zipCode();
    ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());
    return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
  }

  private Product findProduct(ProductId productId) {
    return productCatalogService.ofId(productId)
        .orElseThrow(() -> new ProductNotFoundException());
  }

}