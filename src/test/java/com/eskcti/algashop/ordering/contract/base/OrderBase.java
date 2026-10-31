package com.eskcti.algashop.ordering.contract.base;

import com.eskcti.algashop.ordering.core.application.order.query.OrderDetailOutputTestDataBuilder;
import com.eskcti.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.core.application.checkout.CheckoutApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.eskcti.algashop.ordering.core.ports.in.order.OrderFilter;
import com.eskcti.algashop.ordering.core.application.order.OrderQueryService;
import com.eskcti.algashop.ordering.core.application.security.SecurityChecks;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.order.MyOrdersController;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.order.OrderController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = { OrderController.class, MyOrdersController.class })
public class OrderBase {

        @Autowired
        private WebApplicationContext context;

        @MockitoBean
        private OrderQueryService orderQueryService;

        @MockitoBean
        private BuyNowApplicationService buyNowApplicationService;

        @MockitoBean
        private CheckoutApplicationService checkoutApplicationService;

        @MockitoBean
        private SecurityChecks securityCheck;

        public static final String validOrderId = "01226N0640J7Q";

        public static final String notFoundOrderId = "01226N0693HDH";

        @BeforeEach
        void setUp() {
                Mockito.when(securityCheck.getAuthenticatedUserId())
                                .thenReturn(UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a"));
                Mockito.when(orderQueryService.filter(Mockito.any(OrderFilter.class)))
                                .thenReturn(new PageImpl<>(List.of()));

                Mockito.when(buyNowApplicationService.buyNow(Mockito.any(BuyNowInput.class)))
                                .thenReturn(validOrderId);

                Mockito.when(checkoutApplicationService.checkout(Mockito.any(CheckoutInput.class)))
                                .thenReturn(validOrderId);

                Mockito.when(orderQueryService.findById(validOrderId))
                                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(validOrderId).build());

                Mockito.when(orderQueryService.findById(notFoundOrderId))
                                .thenThrow(new OrderNotFoundException());

                RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

                RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        }

}
