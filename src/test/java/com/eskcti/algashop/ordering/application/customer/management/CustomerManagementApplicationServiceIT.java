package com.eskcti.algashop.ordering.application.customer.management;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

  @Autowired
  private CustomerManagementApplicationService customerManagementApplicationService;

  @Test
  public void shouldRegister() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();

    UUID customerId = customerManagementApplicationService.create(input);
    Assertions.assertThat(customerId).isNotNull();

    CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

    Assertions.assertThat(customerOutput)
        .extracting(
            CustomerOutput::getId,
            CustomerOutput::getFirstName,
            CustomerOutput::getLastName,
            CustomerOutput::getEmail,
            CustomerOutput::getBirthDate)
        .containsExactly(
            customerId,
            "John",
            "Doe",
            "johndoe@email.com",
            LocalDate.of(1991, 7, 5));

    Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
  }

  @Test
  public void shouldUpdate() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

    UUID customerId = customerManagementApplicationService.create(input);
    Assertions.assertThat(customerId).isNotNull();

    customerManagementApplicationService.update(customerId, updateInput);

    CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

    Assertions.assertThat(customerOutput)
        .extracting(
            CustomerOutput::getId,
            CustomerOutput::getFirstName,
            CustomerOutput::getLastName,
            CustomerOutput::getEmail,
            CustomerOutput::getBirthDate)
        .containsExactly(
            customerId,
            "Matt",
            "Damon",
            "johndoe@email.com",
            LocalDate.of(1991, 7, 5));

    Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
  }

  @Test
  public void shouldThrowExceptionWhenCustomerDoesNotExistOnFindById() {
    UUID nonExistentId = UUID.randomUUID();

    Assertions.assertThatThrownBy(() -> customerManagementApplicationService.findById(nonExistentId))
        .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  public void shouldThrowExceptionWhenCustomerDoesNotExistOnUpdate() {
    UUID nonExistentId = UUID.randomUUID();
    CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

    Assertions.assertThatThrownBy(() -> customerManagementApplicationService.update(nonExistentId, updateInput))
        .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  public void shouldDisablePromotionNotificationsWhenUpdating() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer()
        .promotionNotificationsAllowed(true)
        .build();
    CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate()
        .promotionNotificationsAllowed(false)
        .build();

    UUID customerId = customerManagementApplicationService.create(input);
    customerManagementApplicationService.update(customerId, updateInput);

    CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

    Assertions.assertThat(customerOutput.getPromotionNotificationsAllowed()).isFalse();
  }

}
