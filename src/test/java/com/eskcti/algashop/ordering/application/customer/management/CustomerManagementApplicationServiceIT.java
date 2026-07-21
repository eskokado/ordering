package com.eskcti.algashop.ordering.application.customer.management;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.eskcti.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

  @Autowired
  private CustomerManagementApplicationService customerManagementApplicationService;

  @MockitoSpyBean
  private CustomerEventListener customerEventListener;

  @MockitoSpyBean
  private CustomerNotificationService customerNotificationService;

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

    Mockito.verify(customerEventListener)
        .listen(Mockito.any(CustomerRegisteredEvent.class));

    Mockito.verify(customerEventListener, Mockito.never())
        .listen(Mockito.any(CustomerArchivedEvent.class));

    Mockito.verify(customerNotificationService)
        .notifyNewRegistration(Mockito.any(CustomerNotificationService.NotifyNewRegistrationInput.class));
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

  @Test
  public void shouldArchiveCustomer() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    UUID customerId = customerManagementApplicationService.create(input);
    Assertions.assertThat(customerId).isNotNull();

    customerManagementApplicationService.archive(customerId);

    CustomerOutput archivedCustomer = customerManagementApplicationService.findById(customerId);

    Assertions.assertThat(archivedCustomer)
        .isNotNull()
        .extracting(
            CustomerOutput::getFirstName,
            CustomerOutput::getLastName,
            CustomerOutput::getPhone,
            CustomerOutput::getDocument,
            CustomerOutput::getBirthDate,
            CustomerOutput::getPromotionNotificationsAllowed)
        .containsExactly(
            "Anonymous",
            "Anonymous",
            "000-000-0000",
            "000-00-0000",
            null,
            false);

    Assertions.assertThat(archivedCustomer.getEmail()).endsWith("@anonymous.com");
    Assertions.assertThat(archivedCustomer.getArchived()).isTrue();
    Assertions.assertThat(archivedCustomer.getArchivedAt()).isNotNull();

    Assertions.assertThat(archivedCustomer.getAddress()).isNotNull();
    Assertions.assertThat(archivedCustomer.getAddress().getNumber()).isNotNull().isEqualTo("Anonymized");
    Assertions.assertThat(archivedCustomer.getAddress().getComplement()).isNull();
  }

  @Test
  public void shouldThrowCustomerNotFoundExceptionWhenArchivingNonExistingCustomer() {
    UUID nonExistingId = UUID.randomUUID();

    Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
        .isThrownBy(() -> customerManagementApplicationService.archive(nonExistingId));
  }

  @Test
  public void shouldThrowCustomerArchivedExceptionWhenArchivingAlreadyArchivedCustomer() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    UUID customerId = customerManagementApplicationService.create(input);
    Assertions.assertThat(customerId).isNotNull();

    customerManagementApplicationService.archive(customerId);

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customerManagementApplicationService.archive(customerId));
  }

  @Test
  public void shouldChangeEmail() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    UUID customerId = customerManagementApplicationService.create(input);

    customerManagementApplicationService.changeEmail(customerId, "newemail@email.com");

    CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

    Assertions.assertThat(customerOutput.getEmail()).isEqualTo("newemail@email.com");
  }

  @Test
  public void shouldThrowCustomerNotFoundExceptionWhenChangingEmailOfNonExistingCustomer() {
    UUID nonExistingId = UUID.randomUUID();

    Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
        .isThrownBy(() -> customerManagementApplicationService.changeEmail(nonExistingId, "newemail@email.com"));
  }

  @Test
  public void shouldThrowCustomerArchivedExceptionWhenChangingEmailOfArchivedCustomer() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    UUID customerId = customerManagementApplicationService.create(input);

    customerManagementApplicationService.archive(customerId);

    Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
        .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, "newemail@email.com"));
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWhenChangingEmailToInvalidFormat() {
    CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
    UUID customerId = customerManagementApplicationService.create(input);

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, "email-invalido"));
  }

  @Test
  public void shouldThrowCustomerEmailIsInUseExceptionWhenChangingEmailToExistingEmail() {
    UUID firstCustomerId = customerManagementApplicationService.create(
        CustomerInputTestDataBuilder.aCustomer().email("first@email.com").build());
    customerManagementApplicationService.create(
        CustomerInputTestDataBuilder.aCustomer().email("second@email.com").build());

    Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
        .isThrownBy(() -> customerManagementApplicationService.changeEmail(firstCustomerId, "second@email.com"));
  }

}
