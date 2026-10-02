package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
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
import java.util.UUID;

import static com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ShoppingCartControllerIT extends AbstractPresentationIT {

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
    public void shouldCreateShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-shopping-cart.json");

        UUID createdShoppingCart = givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/shopping-carts")
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
    public void shouldAddProductToShoppingCart() {
        var shoppingCartPersistence = existingShoppingCart().items(new HashSet<>())
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();

        shoppingCartRepository.save(shoppingCartPersistence);

        UUID shoppingCartId = shoppingCartPersistence.getId();

        String json = AlgaShopResourceUtils.readContent("json/add-product-to-shopping-cart.json");

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", shoppingCartId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCartPersistenceEntity = shoppingCartRepository.findById(shoppingCartPersistence.getId())
                .orElseThrow();
        Assertions.assertThat(shoppingCartPersistenceEntity.getTotalItems()).isEqualTo(2);
    }

}
