package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;

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
    public void shouldFindShoppingCartById() {
        var shoppingCartPersistence = existingShoppingCart().items(new HashSet<>())
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();

        shoppingCartRepository.save(shoppingCartPersistence);

        UUID shoppingCartId = shoppingCartPersistence.getId();

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/shopping-carts/{shoppingCartId}", shoppingCartId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(shoppingCartId.toString()));
    }

    @Test
    public void shouldListShoppingCartItems() {
        var shoppingCartPersistence = existingShoppingCart()
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();

        shoppingCartRepository.save(shoppingCartPersistence);

        UUID shoppingCartId = shoppingCartPersistence.getId();

        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/shopping-carts/{shoppingCartId}/items", shoppingCartId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("items", Matchers.hasSize(2));
    }
}
