package com.eskcti.algashop.ordering.core.application.checkout;

import com.eskcti.algashop.ordering.core.application.AbstractApplicationIT;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.eskcti.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.core.domain.model.order.Orders;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.eskcti.algashop.ordering.core.domain.model.product.Product;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.utils.WithMockJwt;


class BuyNowApplicationServiceIT extends AbstractApplicationIT {

        @Autowired
        private BuyNowApplicationService buyNowApplicationService;

        @Autowired
        private Orders orders;

        @MockitoSpyBean
        private Customers customers;

        @MockitoBean
        private ProductCatalogService productCatalogService;

        @MockitoBean
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
        public void shouldThrowExceptionWhenCustomerDoesNotExist() {
                Mockito.doReturn(Optional.empty())
                                .when(customers).ofId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        public void shouldThrowAccessDeniedExceptionWhenOrderingForAnotherCustomer() {
                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
                                .customerId(CustomerTestDataBuilder.SECOND_CUSTOMER_ID.value())
                                .build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockJwt(role = "ADMIN")
        public void shouldThrowAccessDeniedExceptionWhenAuthenticatedUserIsNotCustomer() {
                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        public void shouldThrowExceptionWhenProductDoesNotExist() {
                Mockito.when(productCatalogService.ofId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID))
                                .thenReturn(Optional.empty());

                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        public void shouldThrowDomainExceptionWhenCreditCardIdIsMissingAndPaymentMethodIsCreditCard() {
                Product product = ProductTestDataBuilder.aProduct()
                                .id(ProductTestDataBuilder.DEFAULT_PRODUCT_ID)
                                .build();
                Mockito.when(productCatalogService.ofId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID))
                                .thenReturn(Optional.of(product));

                Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
                                .thenReturn(new ShippingCostService.CalculationResult(
                                                new Money("10.00"),
                                                LocalDate.now().plusDays(3)));

                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
                                .creditCardId(null)
                                .build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(DomainException.class)
                                .hasMessage("Credit card id is required");
        }

}
