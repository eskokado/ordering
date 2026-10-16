package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.core.application.utility.Mapper;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;

@ExtendWith(MockitoExtension.class)
class ForObtainingShoppingCartsJpaRepositoryImplTest {

    @Mock
    private ShoppingCartPersistenceEntityRepository persistenceRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private ForObtainingShoppingCartsJpaRepositoryImpl forObtainingShoppingCarts;

    @Test
    void findById_returnsShoppingCartOutput_whenFound() {
        UUID cartId = UUID.randomUUID();
        ShoppingCartPersistenceEntity entity = new ShoppingCartPersistenceEntity();
        ShoppingCartOutput output = ShoppingCartOutput.builder().id(cartId).build();
        when(persistenceRepository.findById(cartId)).thenReturn(Optional.of(entity));
        when(mapper.convert(entity, ShoppingCartOutput.class)).thenReturn(output);

        ShoppingCartOutput result = forObtainingShoppingCarts.findById(cartId);

        assertThat(result).isSameAs(output);
    }

    @Test
    void findById_throwsShoppingCartNotFound_whenMissing() {
        UUID cartId = UUID.randomUUID();
        when(persistenceRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forObtainingShoppingCarts.findById(cartId))
                .isInstanceOf(ShoppingCartNotFoundException.class);
    }

    @Test
    void findByCustomerId_returnsShoppingCartOutput_whenFound() {
        UUID customerId = UUID.randomUUID();
        ShoppingCartPersistenceEntity entity = new ShoppingCartPersistenceEntity();
        ShoppingCartOutput output = ShoppingCartOutput.builder().customerId(customerId).build();
        when(persistenceRepository.findByCustomer_Id(customerId)).thenReturn(Optional.of(entity));
        when(mapper.convert(entity, ShoppingCartOutput.class)).thenReturn(output);

        ShoppingCartOutput result = forObtainingShoppingCarts.findByCustomerId(customerId);

        assertThat(result).isSameAs(output);
    }

    @Test
    void findByCustomerId_throwsShoppingCartNotFound_whenMissing() {
        UUID customerId = UUID.randomUUID();
        when(persistenceRepository.findByCustomer_Id(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forObtainingShoppingCarts.findByCustomerId(customerId))
                .isInstanceOf(ShoppingCartNotFoundException.class);
    }
}
