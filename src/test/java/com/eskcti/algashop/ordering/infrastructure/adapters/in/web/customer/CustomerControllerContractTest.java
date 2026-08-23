package com.eskcti.algashop.ordering.presentation.customer;

import io.restassured.config.JsonConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.eskcti.algashop.ordering.core.application.customer.query.CustomerOutputTestDataBuilder;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerSummaryOutputTestDataBuilder;
import com.eskcti.algashop.ordering.core.application.commons.AddressData;
import com.eskcti.algashop.ordering.core.application.customer.CustomerManagementApplicationService;
import com.eskcti.algashop.ordering.core.application.customer.CustomerQueryService;
import com.eskcti.algashop.ordering.core.application.customer.management.CustomerInput;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerFilter;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerSummaryOutput;
import com.eskcti.algashop.ordering.core.application.shoppingcart.ShoppingCartQueryService;
import com.eskcti.algashop.ordering.core.domain.model.DomainException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerArchivedException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerEmailIsInUseException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ShoppingCartItemOutput;
import com.eskcti.algashop.ordering.core.ports.out.shoppingcart.ShoppingCartOutput;
import com.eskcti.algashop.ordering.presentation.BadGatewayException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerContractTest {

  @Autowired
  private WebApplicationContext context;

  @MockitoBean
  private CustomerManagementApplicationService customerManagementApplicationService;

  @MockitoBean
  private CustomerQueryService customerQueryService;

  @MockitoBean
  private ShoppingCartQueryService shoppingCartQueryService;

  @BeforeEach
  public void setupAll() {
    RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .build());
    RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    RestAssuredMockMvc.config = RestAssuredMockMvc.config()
        .jsonConfig(JsonConfig.jsonConfig()
            .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
  }

  @Test
  public void createCustomerContract() {
    UUID customerId = UUID.randomUUID();
    CustomerOutput customerOutput = CustomerOutputTestDataBuilder.existing().id(customerId).build();
    Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
        .thenReturn(customerId);
    Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
        .thenReturn(customerOutput);

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.CREATED.value())
        .header("Location", Matchers.containsString("/api/v1/customers/" + customerId))
        .body(
            "id", Matchers.notNullValue(),
            "registeredAt", Matchers.notNullValue(),
            "firstName", Matchers.is("John"),
            "lastName", Matchers.is("Doe"),
            "email", Matchers.is("johndoe@email.com"),
            "document", Matchers.is("12345"),
            "phone", Matchers.is("1191234564"),
            "birthDate", Matchers.is("1991-07-05"),
            "promotionNotificationsAllowed", Matchers.is(false),
            "loyaltyPoints", Matchers.is(0),
            "address.street", Matchers.is("Bourbon Street"),
            "address.number", Matchers.is("2000"),
            "address.complement", Matchers.is("apt 122"),
            "address.neighborhood", Matchers.is("North Ville"),
            "address.city", Matchers.is("Yostfort"),
            "address.state", Matchers.is("South Carolina"),
            "address.zipCode", Matchers.is("12321"));
  }

  @Test
  public void createCustomerErrorContract() {
    String jsonInput = """
        {
          "firstName": "",
          "lastName": "",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(jsonInput)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body(
            "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
            "type", Matchers.is("/errors/invalid-fields"),
            "title", Matchers.notNullValue(),
            "detail", Matchers.notNullValue(),
            "fields", Matchers.notNullValue());

  }

  @Test
  public void findCustomersContract() {
    int sizeLimit = 5;
    int pageNumber = 0;

    CustomerSummaryOutput customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
    CustomerSummaryOutput customer2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

    Mockito.when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
        .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .queryParam("size", sizeLimit)
        .queryParam("page", pageNumber)
        .when()
        .get("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.OK.value())
        .body(
            "number", Matchers.equalTo(pageNumber),
            "size", Matchers.equalTo(2),
            "totalPages", Matchers.equalTo(1),
            "totalElements", Matchers.equalTo(2),

            "content[0].id", Matchers.equalTo(customer1.getId().toString()),
            "content[0].firstName", Matchers.is(customer1.getFirstName()),
            "content[0].lastName", Matchers.is(customer1.getLastName()),
            "content[0].email", Matchers.is(customer1.getEmail()),
            "content[0].document", Matchers.is(customer1.getDocument()),
            "content[0].phone", Matchers.is(customer1.getPhone()),
            "content[0].birthDate", Matchers.is(customer1.getBirthDate().toString()),
            "content[0].loyaltyPoints", Matchers.is(customer1.getLoyaltyPoints()),
            "content[0].promotionNotificationsAllowed", Matchers.is(customer1.getPromotionNotificationsAllowed()),
            "content[0].archived", Matchers.is(customer1.getArchived()),
            "content[0].registeredAt", Matchers.is(formatter.format(customer1.getRegisteredAt())),

            "content[1].id", Matchers.equalTo(customer2.getId().toString()),
            "content[1].firstName", Matchers.is(customer2.getFirstName()),
            "content[1].lastName", Matchers.is(customer2.getLastName()),
            "content[1].email", Matchers.is(customer2.getEmail()),
            "content[1].document", Matchers.is(customer2.getDocument()),
            "content[1].phone", Matchers.is(customer2.getPhone()),
            "content[1].birthDate", Matchers.is(customer2.getBirthDate().toString()),
            "content[1].loyaltyPoints", Matchers.is(customer2.getLoyaltyPoints()),
            "content[1].promotionNotificationsAllowed", Matchers.is(customer2.getPromotionNotificationsAllowed()),
            "content[1].archived", Matchers.is(customer2.getArchived()),
            "content[1].registeredAt", Matchers.is(formatter.format(customer2.getRegisteredAt()))

        );
  }

  @Test
  public void findCustomerByIdContract() {
    CustomerOutput customerOutput = CustomerOutputTestDataBuilder.existing().build();
    UUID customerId = customerOutput.getId();

    Mockito.when(customerQueryService.findById(customerId))
        .thenReturn(customerOutput);

    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.OK.value())
        .body(
            "id", Matchers.equalTo(customerId.toString()),
            "firstName", Matchers.is(customerOutput.getFirstName()),
            "lastName", Matchers.is(customerOutput.getLastName()),
            "email", Matchers.is(customerOutput.getEmail()),
            "document", Matchers.is(customerOutput.getDocument()),
            "phone", Matchers.is(customerOutput.getPhone()),
            "birthDate", Matchers.is(customerOutput.getBirthDate().toString()),
            "promotionNotificationsAllowed", Matchers.is(customerOutput.getPromotionNotificationsAllowed()),
            "loyaltyPoints", Matchers.is(customerOutput.getLoyaltyPoints()),
            "registeredAt", Matchers.is(formatter.format(customerOutput.getRegisteredAt())),
            "address.street", Matchers.is(customerOutput.getAddress().getStreet()),
            "address.number", Matchers.is(customerOutput.getAddress().getNumber()),
            "address.complement", Matchers.is(customerOutput.getAddress().getComplement()),
            "address.neighborhood", Matchers.is(customerOutput.getAddress().getNeighborhood()),
            "address.city", Matchers.is(customerOutput.getAddress().getCity()),
            "address.state", Matchers.is(customerOutput.getAddress().getState()),
            "address.zipCode", Matchers.is(customerOutput.getAddress().getZipCode()));
  }

  @Test
  public void findCustomerShoppingCartContract() {
    UUID customerId = UUID.randomUUID();
    UUID shoppingCartId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    ShoppingCartItemOutput item = ShoppingCartItemOutput.builder()
        .id(itemId)
        .productId(productId)
        .name("Notebook X11")
        .price(new BigDecimal("1000.00"))
        .quantity(2)
        .totalAmount(new BigDecimal("2000.00"))
        .available(true)
        .build();

    ShoppingCartOutput shoppingCartOutput = ShoppingCartOutput.builder()
        .id(shoppingCartId)
        .customerId(customerId)
        .totalItems(2)
        .totalAmount(new BigDecimal("2000.00"))
        .items(List.of(item))
        .build();

    Mockito.when(shoppingCartQueryService.findByCustomerId(customerId))
        .thenReturn(shoppingCartOutput);

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/v1/customers/{customerId}/shopping-cart", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.OK.value())
        .body(
            "id", Matchers.equalTo(shoppingCartId.toString()),
            "customerId", Matchers.equalTo(customerId.toString()),
            "totalItems", Matchers.is(2),
            "totalAmount", Matchers.comparesEqualTo(new BigDecimal("2000.00")),
            "items[0].id", Matchers.equalTo(itemId.toString()),
            "items[0].productId", Matchers.equalTo(productId.toString()),
            "items[0].name", Matchers.is("Notebook X11"),
            "items[0].price", Matchers.comparesEqualTo(new BigDecimal("1000.00")),
            "items[0].quantity", Matchers.is(2),
            "items[0].totalAmount", Matchers.comparesEqualTo(new BigDecimal("2000.00")),
            "items[0].available", Matchers.is(true));
  }

  @Test
  public void findByIdError404Contract() {
    UUID invalidCustomerId = UUID.randomUUID();

    Mockito.when(customerQueryService.findById(invalidCustomerId))
        .thenThrow(CustomerNotFoundException.class);

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/v1/customers/{customerId}", invalidCustomerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(
            "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
            "type", Matchers.is("/errors/not-found"),
            "title", Matchers.notNullValue());

  }

  @Test
  public void createCustomerError409Contract() {
    Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
        .thenThrow(CustomerEmailIsInUseException.class);

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.CONFLICT.value())
        .body(
            "status", Matchers.is(HttpStatus.CONFLICT.value()),
            "type", Matchers.is("/errors/conflict"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void createCustomerError502Contract() {
    Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
        .thenThrow(new BadGatewayException("Product Catalog API Bad Gateway", new RuntimeException("upstream")));

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.BAD_GATEWAY.value())
        .body(
            "status", Matchers.is(HttpStatus.BAD_GATEWAY.value()),
            "type", Matchers.is("/errors/bad-gateway"),
            "title", Matchers.notNullValue(),
            "detail", Matchers.is("Product Catalog API Bad Gateway"));
  }

  @Test
  public void createCustomerError422Contract() {
    Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
        .thenThrow(DomainException.class);

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(
            "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            "type", Matchers.is("/errors/unprocessable-entity"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void createCustomerError500Contract() {
    Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
        .thenThrow(RuntimeException.class);

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/api/v1/customers")
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body(
            "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            "type", Matchers.is("/errors/internal"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void updateCustomerContract() {
    CustomerOutput customer = CustomerOutputTestDataBuilder.existing().build();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    AddressData address = customer.getAddress();

    UUID customerId = UUID.randomUUID();
    Mockito.doNothing().when(customerManagementApplicationService)
        .update(Mockito.eq(customerId), Mockito.any());
    Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
        .thenReturn(customer);

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .put("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .statusCode(HttpStatus.OK.value())
        .body(
            "id", Matchers.equalTo(customer.getId().toString()),
            "firstName", Matchers.equalTo(customer.getFirstName()),
            "lastName", Matchers.is(customer.getLastName()),
            "email", Matchers.is(customer.getEmail()),
            "document", Matchers.is(customer.getDocument()),
            "phone", Matchers.is(customer.getPhone()),
            "birthDate", Matchers.is(customer.getBirthDate().toString()),
            "loyaltyPoints", Matchers.is(customer.getLoyaltyPoints()),
            "promotionNotificationsAllowed", Matchers.is(customer.getPromotionNotificationsAllowed()),
            "archived", Matchers.is(customer.getArchived()),
            "registeredAt", Matchers.is(formatter.format(customer.getRegisteredAt())),
            "address.street", Matchers.is(address.getStreet()),
            "address.number", Matchers.is(address.getNumber()),
            "address.complement", Matchers.is(address.getComplement()),
            "address.neighborhood", Matchers.is(address.getNeighborhood()),
            "address.city", Matchers.is(address.getCity()),
            "address.state", Matchers.is(address.getState()),
            "address.zipCode", Matchers.is(address.getZipCode()));
  }

  @Test
  public void updateCustomerError400Contract() {
    UUID customerId = UUID.randomUUID();

    String jsonInput = """
        {
          "firstName": "",
          "lastName": "",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(jsonInput)
        .when()
        .put("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body(
            "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
            "type", Matchers.is("/errors/invalid-fields"),
            "title", Matchers.notNullValue(),
            "detail", Matchers.notNullValue(),
            "fields", Matchers.notNullValue());
  }

  @Test
  public void updateCustomerError404Contract() {
    UUID invalidCustomerId = UUID.randomUUID();

    Mockito.doThrow(CustomerNotFoundException.class)
        .when(customerManagementApplicationService)
        .update(Mockito.eq(invalidCustomerId), Mockito.any());

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .put("/api/v1/customers/{customerId}", invalidCustomerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(
            "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
            "type", Matchers.is("/errors/not-found"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void updateCustomerError409Contract() {
    UUID customerId = UUID.randomUUID();

    Mockito.doThrow(CustomerEmailIsInUseException.class)
        .when(customerManagementApplicationService)
        .update(Mockito.eq(customerId), Mockito.any());

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .put("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.CONFLICT.value())
        .body(
            "status", Matchers.is(HttpStatus.CONFLICT.value()),
            "type", Matchers.is("/errors/conflict"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void updateCustomerError422Contract() {
    UUID customerId = UUID.randomUUID();

    Mockito.doThrow(DomainException.class)
        .when(customerManagementApplicationService)
        .update(Mockito.eq(customerId), Mockito.any());

    String jsonInput = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .body(jsonInput)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .put("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(
            "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            "type", Matchers.is("/errors/unprocessable-entity"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void deleteCustomerContract() {
    UUID customerId = UUID.randomUUID();

    Mockito.doNothing().when(customerManagementApplicationService)
        .archive(customerId);

    RestAssuredMockMvc
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .delete("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  public void deleteCustomerError404Contract() {
    UUID invalidCustomerId = UUID.randomUUID();

    Mockito.doThrow(CustomerNotFoundException.class)
        .when(customerManagementApplicationService)
        .archive(invalidCustomerId);

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/api/v1/customers/{customerId}", invalidCustomerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(
            "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
            "type", Matchers.is("/errors/not-found"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void deleteCustomerError422Contract() {
    UUID customerId = UUID.randomUUID();

    Mockito.doThrow(CustomerArchivedException.class)
        .when(customerManagementApplicationService)
        .archive(customerId);

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(
            "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            "type", Matchers.is("/errors/unprocessable-entity"),
            "title", Matchers.notNullValue());
  }

  @Test
  public void deleteCustomerError500Contract() {
    UUID customerId = UUID.randomUUID();

    Mockito.doThrow(RuntimeException.class)
        .when(customerManagementApplicationService)
        .archive(customerId);

    RestAssuredMockMvc
        .given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/api/v1/customers/{customerId}", customerId)
        .then()
        .assertThat()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body(
            "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            "type", Matchers.is("/errors/internal"),
            "title", Matchers.notNullValue());
  }

}