package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.domain.model.customer.Customers;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingService;

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