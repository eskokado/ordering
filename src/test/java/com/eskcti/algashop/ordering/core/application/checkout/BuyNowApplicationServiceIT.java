package com.eskcti.algashop.ordering.core.application.checkout;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.eskcti.algashop.ordering.core.application.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;
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

@SpringBootTest

@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class BuyNowApplicationServiceIT {

        @Autowired
        private BuyNowApplicationService buyNowApplicationService;

        @Autowired
        private Orders orders;

        @Autowired
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
                Product product = ProductTestDataBuilder.aProduct()
                                .id(ProductTestDataBuilder.DEFAULT_PRODUCT_ID)
                                .build();
                Mockito.when(productCatalogService.ofId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID))
                                .thenReturn(Optional.of(product));

                BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
                                .customerId(new CustomerId().value())
                                .build();

                Assertions.assertThatThrownBy(() -> buyNowApplicationService.buyNow(input))
                                .isInstanceOf(CustomerNotFoundException.class);
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