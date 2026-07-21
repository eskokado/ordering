package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ShoppingCartsPersistenceProviderTest {

  @Mock
  private ShoppingCartPersistenceEntityRepository repository;

  @Mock
  private ShoppingCartPersistenceEntityAssembler assembler;

  @Mock
  private ShoppingCartPersistenceEntityDisassembler disassembler;

  @Mock
  private EntityManager entityManager;

  private ShoppingCartsPersistenceProvider provider;

  @BeforeEach
  void setUp() {
    provider = new ShoppingCartsPersistenceProvider(repository, assembler, disassembler, entityManager);
  }

  @Test
  void givenExistingShoppingCartId_whenOfId_shouldReturnShoppingCart() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
    ShoppingCartPersistenceEntity persistenceEntity = new ShoppingCartPersistenceEntity();

    when(repository.findById(cart.id().value())).thenReturn(Optional.of(persistenceEntity));
    when(disassembler.toDomainEntity(persistenceEntity)).thenReturn(cart);

    Optional<ShoppingCart> found = provider.ofId(cart.id());

    assertThat(found).isPresent();
    assertThat(found.get()).isEqualTo(cart);
  }

  @Test
  void givenNonExistentShoppingCartId_whenOfId_shouldReturnEmpty() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

    when(repository.findById(cart.id().value())).thenReturn(Optional.empty());

    Optional<ShoppingCart> found = provider.ofId(cart.id());

    assertThat(found).isEmpty();
  }

  @Test
  void givenShoppingCart_whenAddNew_shouldInsert() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
    ShoppingCartPersistenceEntity persistenceEntity = new ShoppingCartPersistenceEntity();

    when(repository.findById(cart.id().value())).thenReturn(Optional.empty());
    when(assembler.fromDomain(cart)).thenReturn(persistenceEntity);
    when(repository.saveAndFlush(persistenceEntity)).thenReturn(persistenceEntity);

    provider.add(cart);

    verify(repository).findById(cart.id().value());
    verify(assembler).fromDomain(cart);
    verify(repository).saveAndFlush(persistenceEntity);
    assertThat(cart.domainEvents()).isEmpty();
  }

  @Test
  void givenShoppingCart_whenAddExisting_shouldUpdate() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
    ShoppingCartPersistenceEntity persistenceEntity = new ShoppingCartPersistenceEntity();

    when(repository.findById(cart.id().value())).thenReturn(Optional.of(persistenceEntity));
    when(assembler.merge(eq(persistenceEntity), eq(cart))).thenReturn(persistenceEntity);
    when(repository.saveAndFlush(persistenceEntity)).thenReturn(persistenceEntity);

    provider.add(cart);

    verify(entityManager).detach(persistenceEntity);
    verify(repository).saveAndFlush(persistenceEntity);
    assertThat(cart.domainEvents()).isEmpty();
  }

  @Test
  void givenShoppingCart_whenRemove_shouldCallRepositoryDelete() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

    provider.remove(cart);

    verify(repository).deleteById(cart.id().value());
  }

  @Test
  void givenShoppingCartId_whenRemove_shouldCallRepositoryDelete() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

    provider.remove(cart.id());

    verify(repository).deleteById(cart.id().value());
  }

  @Test
  void givenCustomerId_whenOfCustomer_shouldReturnShoppingCart() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
    ShoppingCartPersistenceEntity persistenceEntity = new ShoppingCartPersistenceEntity();

    when(repository.findByCustomer_Id(cart.customerId().value())).thenReturn(Optional.of(persistenceEntity));
    when(disassembler.toDomainEntity(persistenceEntity)).thenReturn(cart);

    Optional<ShoppingCart> found = provider.ofCustomer(cart.customerId());

    assertThat(found).isPresent();
    assertThat(found.get()).isEqualTo(cart);
  }

  @Test
  void whenCount_thenReturnRepositoryCount() {
    when(repository.count()).thenReturn(5L);

    long count = provider.count();

    assertThat(count).isEqualTo(5);
    verify(repository).count();
  }

  @Test
  void whenExists_thenReturnRepositoryExists() {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

    when(repository.existsById(cart.id().value())).thenReturn(true);

    assertThat(provider.exists(cart.id())).isTrue();
  }

  @Test
  void shouldUpdateShoppingCartVersionFromPersistenceEntity() throws Exception {
    ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
    ShoppingCartsPersistenceProvider provider = new ShoppingCartsPersistenceProvider(null, null, null, null);
    Method updateVersion = ShoppingCartsPersistenceProvider.class.getDeclaredMethod("updateVersion",
        ShoppingCart.class, ShoppingCartPersistenceEntity.class);
    updateVersion.setAccessible(true);

    ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
        .existingShoppingCart()
        .version(7L)
        .build();

    updateVersion.invoke(provider, cart, persistenceEntity);

    assertThat(cart.version()).isEqualTo(7L);
  }

  @Test
  void shouldPropagateExceptionWhenUpdateVersionFails() throws Exception {
    ShoppingCart cart = new ShoppingCartWithoutDeclaredVersion(ShoppingCartTestDataBuilder.aShoppingCart().build());
    ShoppingCartsPersistenceProvider provider = new ShoppingCartsPersistenceProvider(null, null, null, null);
    Method updateVersion = ShoppingCartsPersistenceProvider.class.getDeclaredMethod("updateVersion",
        ShoppingCart.class, ShoppingCartPersistenceEntity.class);
    updateVersion.setAccessible(true);

    ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
        .existingShoppingCart()
        .version(7L)
        .build();

    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      updateVersion.invoke(provider, cart, persistenceEntity);
    });

    assertInstanceOf(NoSuchFieldException.class, exception.getCause());
  }

  private static final class ShoppingCartWithoutDeclaredVersion extends ShoppingCart {

    private ShoppingCartWithoutDeclaredVersion(ShoppingCart source) {
      super(
          source.id(),
          source.version(),
          source.customerId(),
          source.totalAmount(),
          source.totalItems(),
          source.createdAt(),
          Set.copyOf(source.items()));
    }
  }
}
