package com.eskcti.algashop.ordering.domain.model.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.eskcti.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.repository.Customers;
import com.eskcti.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

  @InjectMocks
  private ShoppingService shoppingService;

  @Mock
  private ShoppingCarts shoppingCarts;

  @Mock
  private Customers customers;

  @Test
  void givenExistingCustomerAndNoShoppingCart_whenStartShopping_shouldReturnNewShoppingCart() {
    CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

    when(customers.exists(customerId)).thenReturn(true);
    when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.empty());

    ShoppingCart newShoppingCart = shoppingService.startShopping(customerId);

    assertThat(newShoppingCart).isNotNull();
    assertThat(newShoppingCart.customerId()).isEqualTo(customerId);
    assertThat(newShoppingCart.isEmpty()).isTrue();
    assertThat(newShoppingCart.totalAmount())
        .isEqualTo(Money.ZERO);
    assertThat(newShoppingCart.totalItems())
        .isEqualTo(Quantity.ZERO);

    verify(customers).exists(customerId);
    verify(shoppingCarts).ofCustomer(customerId);
  }

  @Test
  void givenNonExistingCustomer_whenStartShopping_shouldThrowCustomerNotFoundException() {
    CustomerId customerId = new CustomerId();

    when(customers.exists(customerId)).thenReturn(false);

    assertThatExceptionOfType(CustomerNotFoundException.class)
        .isThrownBy(() -> shoppingService.startShopping(customerId));

    verify(customers).exists(customerId);
    verify(shoppingCarts, never()).ofCustomer(any());
  }

  @Test
  void givenExistingCustomerAndExistingShoppingCart_whenStartShopping_shouldThrowCustomerAlreadyHaveShoppingCartException() {
    CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
    ShoppingCart existingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();

    when(customers.exists(customerId)).thenReturn(true);
    when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(existingCart));

    assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
        .isThrownBy(() -> shoppingService.startShopping(customerId));

    verify(customers).exists(customerId);
    verify(shoppingCarts).ofCustomer(customerId);
  }
}