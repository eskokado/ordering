package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.eskcti.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
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

import java.util.UUID;

public class CustomerControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    public void setup() {
        initDatabase();
    }

    private void initDatabase() {
        customerRepository.saveAndFlush(
                CustomerPersistenceEntityTestDataBuilder.aCustomer().id(validCustomerId).build());
    }

    @Test
    public void shouldCreateCustomer() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        UUID createdCustomerId = givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()))
                .extract()
                .jsonPath().getUUID("id");

        Assertions.assertThat(customerRepository.existsById(createdCustomerId)).isTrue();
    }

    @Test
    public void shouldArchiveCustomer() {
        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/{customerId}", validCustomerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Assertions.assertThat(customerRepository.existsById(validCustomerId)).isTrue();
        Assertions.assertThat(customerRepository.findById(validCustomerId).orElseThrow().getArchived()).isTrue();
    }
}
