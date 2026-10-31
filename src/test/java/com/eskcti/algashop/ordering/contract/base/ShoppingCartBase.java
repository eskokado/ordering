package com.eskcti.algashop.ordering.contract.base;

import com.eskcti.algashop.ordering.core.application.security.SecurityChecks;
import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartQueryService;
import com.eskcti.algashop.ordering.core.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.MyShoppingCartController;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.ShoppingCartController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = { ShoppingCartController.class, MyShoppingCartController.class })
public class ShoppingCartBase {

        @Autowired
        private WebApplicationContext context;

        @MockitoBean
        private ShoppingCartManagementApplicationService managementService;

        @MockitoBean
        private ShoppingCartQueryService queryService;

        @MockitoBean
        private SecurityChecks securityChecks;

        public static final UUID validShoppingCartId = UUID.fromString("ad265aa3-c77d-46e9-9782-b70c487c1e17");

        public static final UUID notFoundShoppingCartId = UUID.fromString("e2103964-5353-4910-81ee-212a40a2ca70");

        public static final UUID notFoundProductId = UUID.fromString("21651a12-b126-4213-ac21-19f66ff4642e");

        public static final UUID validCustomerId = UUID.fromString("f5ab7a1e-37da-41e1-892b-a1d38275c2f2");

        @BeforeEach
        void setUp() {
                RestAssuredMockMvc.mockMvc(
                                MockMvcBuilders.webAppContextSetup(context)
                                                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                                                .build());

                RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

                Mockito.when(securityChecks.getAuthenticatedUserId()).thenReturn(validCustomerId);

                Mockito.when(queryService.findById(validShoppingCartId))
                                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart().id(validShoppingCartId)
                                                .build());

                Mockito.when(queryService.findById(notFoundShoppingCartId))
                                .thenThrow(new ShoppingCartNotFoundException(notFoundShoppingCartId));

                Mockito.when(queryService.findByCustomerId(validCustomerId))
                                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart().id(validShoppingCartId)
                                                .build());

                Mockito.when(managementService.createNew(validCustomerId))
                                .thenReturn(validShoppingCartId);

                Mockito.doThrow(new ProductNotFoundException())
                                .when(managementService)
                                .addItem(Mockito.argThat(input -> input != null
                                                && notFoundProductId.equals(input.getProductId())));
        }
}
