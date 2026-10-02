package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.order;

import com.eskcti.algashop.ordering.core.application.checkout.BuyNowInputTestDataBuilder;
import com.eskcti.algashop.ordering.core.application.checkout.BuyNowInput;
import com.eskcti.algashop.ordering.core.application.order.query.OrderDetailOutput;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.eskcti.algashop.ordering.utils.AlgaShopResourceUtils;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityRepository;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerIT extends AbstractPresentationIT {

  @Autowired
  private CustomerPersistenceEntityRepository customerRepository;

  @Autowired
  private OrderPersistenceEntityRepository orderRepository;

  @Autowired
  private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

  private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
  private static final UUID validProductId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
  private static final UUID validShoppingCartId = UUID.fromString("ad265aa3-c77d-46e9-9782-b70c487c1e17");

  @BeforeEach
  public void setup() {
    initDatabase();
    initWireMock();
  }

  private void initDatabase() {
    customerRepository.saveAndFlush(
        CustomerPersistenceEntityTestDataBuilder.aCustomer().id(validCustomerId).build());
  }

  @Test
  public void shouldCreateOrderUsingProduct() {
    String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");
    String createdOrderId = givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-product.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.CREATED.value())
        .body("id", Matchers.not(Matchers.emptyString()),
            "customer.id", Matchers.is(validCustomerId.toString()))
        .extract()
        .jsonPath().getString("id");

    boolean orderExists = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());
    Assertions.assertThat(orderExists).isTrue();
  }

  @Test
  public void shouldCreateOrderUsingProduct_DTO() {
    BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
        .productId(validProductId)
        .customerId(validCustomerId)
        .build();

    OrderDetailOutput orderDetailOutput = givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-product.v1+json")
        .body(input)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.CREATED.value())
        .body("id", Matchers.not(Matchers.emptyString()),
            "customer.id", Matchers.is(validCustomerId.toString()))
        .extract()
        .body().as(OrderDetailOutput.class);

    Assertions.assertThat(orderDetailOutput.getCustomer().getId()).isEqualTo(validCustomerId);

    boolean orderExists = orderRepository.existsById(new OrderId(orderDetailOutput.getId()).value().toLong());
    Assertions.assertThat(orderExists).isTrue();
  }

  @Test
  public void shouldCreateOrderUsingShoppingCart() {
    shoppingCartRepository.saveAndFlush(
        existingShoppingCart()
            .id(validShoppingCartId)
            .customer(customerRepository.getReferenceById(validCustomerId))
            .build());

    String json = AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart.json");
    String createdOrderId = givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-shopping-cart.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.CREATED.value())
        .body("id", Matchers.not(Matchers.emptyString()),
            "customer.id", Matchers.is(validCustomerId.toString()))
        .extract()
        .jsonPath().getString("id");

    boolean orderExists = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());
    Assertions.assertThat(orderExists).isTrue();
  }

  @Test
  public void shouldNotCreateOrderUsingProductWhenProductAPIIsUnavailable() {
    String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

    wireMockProductCatalog.stop();

    givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-product.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());

  }

  @Test
  public void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound() {
    String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");
    givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-product.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  @Test
  public void shouldNotCreateOrderUsingProductWhenProductWasNotFound() {
    String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-product.json");
    givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-product.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  @Test
  public void shouldNotCreateOrderUsingShoppingCartWhenShoppingCartWasNotFound() {
    String json = AlgaShopResourceUtils.readContent(
        "json/create-order-with-shopping-cart-and-invalid-cart.json");
    givenAuthenticated()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType("application/vnd.order-with-shopping-cart.v1+json")
        .body(json)
        .when()
        .post("/api/v1/orders")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }
}
