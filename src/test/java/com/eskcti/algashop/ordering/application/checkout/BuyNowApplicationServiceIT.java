package com.eskcti.algashop.ordering.application.checkout;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.order.Orders;
import com.eskcti.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;

@SpringBootTest
@Transactional
class BuyNowApplicationServiceIT {

  @Autowired
  private BuyNowApplicationService buyNowApplicationService;

  @Autowired
  private Orders orders;

  @Autowired
  private Customers customers;

  @MockitoBean(name = "productCatalogServiceFakeImpl")
  private ProductCatalogService productCatalogService;

  @MockitoBean(name = "shippingCostServiceRapidexImpl")
  private ShippingCostService shippingCostService;

  @BeforeEach
  public void setup() {
    if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
      customers.add(CustomerTestDataBuilder.existingCustomer()
          .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
          .build());
    }
  }

  @Test
  public void shouldBuyNow() {
    Product product = ProductTestDataBuilder.aProduct()
        .id(ProductTestDataBuilder.DEFAULT_PRODUCT_ID)
        .build();
    Mockito.when(productCatalogService.ofId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID))
        .thenReturn(Optional.of(product));

    Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
        .thenReturn(new ShippingCostService.CalculationResult(
            new Money("10.00"),
            LocalDate.now().plusDays(3)));

    BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

    String orderId = buyNowApplicationService.buyNow(input);

    Assertions.assertThat(orderId).isNotBlank();
    Assertions.assertThat(orders.exists(new OrderId(orderId))).isTrue();
  }

  @Test
  public void shouldThrowExceptionWhenProductDoesNotExist() {
    Mockito.when(productCatalogService.ofId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID))
        .thenReturn(Optional.empty());

    BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

    Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
        .isInstanceOf(ProductNotFoundException.class);
  }

}