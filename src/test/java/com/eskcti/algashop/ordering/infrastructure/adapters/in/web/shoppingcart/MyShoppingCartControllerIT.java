package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.utils.AlgaShopResourceUtils;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityTestDataBuilder.existingItem;
import static com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MyShoppingCartControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

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
    public void shouldCreateShoppingCartForAuthenticatedCustomer() {
        UUID createdShoppingCart = givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/customers/me/shopping-cart")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()))
                .extract()
                .jsonPath().getUUID("id");

        Assertions.assertThat(shoppingCartRepository.existsById(createdShoppingCart)).isTrue();
    }

    @Test
    public void shouldFindAuthenticatedCustomerShoppingCart() {
        ShoppingCartPersistenceEntity shoppingCart = existingCart();

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me/shopping-cart")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(shoppingCart.getId().toString()));
    }

    @Test
    public void shouldListAuthenticatedCustomerShoppingCartItems() {
        existingCart();

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("items", Matchers.hasSize(2));
    }

    @Test
    public void shouldAddProductToAuthenticatedCustomerShoppingCart() {
        ShoppingCartPersistenceEntity shoppingCart = existingShoppingCart()
                .items(new HashSet<>())
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();
        shoppingCartRepository.saveAndFlush(shoppingCart);
        String json = AlgaShopResourceUtils.readContent("json/add-product-to-shopping-cart.json");

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCartPersistenceEntity = shoppingCartRepository.findById(shoppingCart.getId())
                .orElseThrow();
        Assertions.assertThat(shoppingCartPersistenceEntity.getTotalItems()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveItemFromAuthenticatedCustomerShoppingCart() {
        UUID itemId = UUID.randomUUID();
        var item = existingItem().id(itemId).build();
        ShoppingCartPersistenceEntity shoppingCart = existingShoppingCart()
                .items(Set.of(item))
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();
        shoppingCartRepository.saveAndFlush(shoppingCart);

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/me/shopping-cart/items/{itemId}", itemId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCartPersistenceEntity = shoppingCartRepository.findById(shoppingCart.getId())
                .orElseThrow();
        Assertions.assertThat(shoppingCartPersistenceEntity.getTotalItems()).isZero();
    }

    @Test
    public void shouldEmptyAuthenticatedCustomerShoppingCart() {
        ShoppingCartPersistenceEntity shoppingCart = existingCart();

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCartPersistenceEntity = shoppingCartRepository.findById(shoppingCart.getId())
                .orElseThrow();
        Assertions.assertThat(shoppingCartPersistenceEntity.getTotalItems()).isZero();
    }

    private ShoppingCartPersistenceEntity existingCart() {
        ShoppingCartPersistenceEntity shoppingCart = existingShoppingCart()
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();

        return shoppingCartRepository.saveAndFlush(shoppingCart);
    }
}
