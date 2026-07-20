package com.eskcti.algashop.ordering.infrastructure.notification.customer;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;

@ExtendWith(MockitoExtension.class)
class CustomerNotificationServiceFakeImplTest {

  @Mock
  private Customers customers;

  @InjectMocks
  private CustomerNotificationServiceFakeImpl customerNotificationService;

  @Test
  void shouldNotifyNewRegistrationWhenCustomerExists() {
    Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
    CustomerId customerId = customer.id();

    when(customers.ofId(customerId)).thenReturn(Optional.of(customer));

    assertThatCode(() -> customerNotificationService.notifyNewRegistration(customerId.value()))
        .doesNotThrowAnyException();

    verify(customers).ofId(customerId);
  }

  @Test
  void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
    CustomerId customerId = new CustomerId();

    when(customers.ofId(customerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerNotificationService.notifyNewRegistration(customerId.value()))
        .isInstanceOf(CustomerNotFoundException.class);

    verify(customers).ofId(customerId);
  }
}
