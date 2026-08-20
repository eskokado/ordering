package com.eskcti.algashop.ordering.contract.base;

import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartQueryService;
import com.eskcti.algashop.ordering.core.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.ShoppingCartController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = ShoppingCartController.class)
public class ShoppingCartBase {

        @Autowired
        private WebApplicationContext context;

        @MockitoBean
        private ShoppingCartManagementApplicationService managementService;

        @MockitoBean
        private ShoppingCartQueryService queryService;

        public static final UUID validShoppingCartId = UUID.fromString("ad265aa3-c77d-46e9-9782-b70c487c1e17");

        public static final UUID notFoundShoppingCartId = UUID.fromString("e2103964-5353-4910-81ee-212a40a2ca70");

        public static final UUID notFoundCustomerId = UUID.fromString("73677343-9c25-4bff-a1d8-fea3830b6d97");

        public static final UUID notFoundProductId = UUID.fromString("21651a12-b126-4213-ac21-19f66ff4642e");

        @BeforeEach
        void setUp() {
                RestAssuredMockMvc.mockMvc(
                                MockMvcBuilders.webAppContextSetup(context)
                                                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                                                .build());

                RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

                Mockito.when(queryService.findById(validShoppingCartId))
                                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart().id(validShoppingCartId)
                                                .build());

                Mockito.when(queryService.findById(notFoundShoppingCartId))
                                .thenThrow(new ShoppingCartNotFoundException());

                Mockito.when(managementService.createNew(Mockito.any(UUID.class)))
                                .thenReturn(validShoppingCartId);

                Mockito.when(managementService.createNew(notFoundCustomerId))
                                .thenThrow(new CustomerNotFoundException());

                Mockito.doThrow(new ProductNotFoundException())
                                .when(managementService)
                                .addItem(Mockito.argThat(input -> input != null
                                                && notFoundProductId.equals(input.getProductId())));
        }
}
