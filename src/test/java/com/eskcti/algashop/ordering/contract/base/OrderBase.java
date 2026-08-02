package com.eskcti.algashop.ordering.contract.base;

import com.eskcti.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.eskcti.algashop.ordering.application.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.eskcti.algashop.ordering.application.checkout.CheckoutInput;
import com.eskcti.algashop.ordering.application.order.query.OrderDetailOutputTestDataBuilder;
import com.eskcti.algashop.ordering.application.order.query.OrderFilter;
import com.eskcti.algashop.ordering.application.order.query.OrderQueryService;
import com.eskcti.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.eskcti.algashop.ordering.presentation.OrderController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = OrderController.class)
public class OrderBase {

  @Autowired
  private WebApplicationContext context;

  @MockitoBean
  private OrderQueryService orderQueryService;

  @MockitoBean
  private BuyNowApplicationService buyNowApplicationService;

  @MockitoBean
  private CheckoutApplicationService checkoutApplicationService;

  public static final String validOrderId = "01226N0640J7Q";

  public static final String notFoundOrderId = "01226N0693HDH";

  @BeforeEach
  void setUp() {
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
