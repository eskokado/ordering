package com.eskcti.algashop.ordering.infrastructure.persistence.shoppingcart;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.product.Product;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItem;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartUpdateProvider;
import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import com.eskcti.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.eskcti.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;

@DataJpaTest
@Import({
    ShoppingCartUpdateProvider.class,
    ShoppingCartsPersistenceProvider.class,
    ShoppingCartPersistenceEntityAssembler.class,
    ShoppingCartPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class ShoppingCartUpdateProviderIT {

  private ShoppingCartsPersistenceProvider persistenceProvider;
  private CustomersPersistenceProvider customersPersistenceProvider;
  private ShoppingCartPersistenceEntityRepository entityRepository;

  private ShoppingCartUpdateProvider shoppingCartUpdateProvider;

  @Autowired
  public ShoppingCartUpdateProviderIT(ShoppingCartsPersistenceProvider persistenceProvider,
      CustomersPersistenceProvider customersPersistenceProvider,
      ShoppingCartPersistenceEntityRepository entityRepository,
      ShoppingCartUpdateProvider shoppingCartUpdateProvider) {
    this.persistenceProvider = persistenceProvider;
    this.customersPersistenceProvider = customersPersistenceProvider;
    this.entityRepository = entityRepository;
    this.shoppingCartUpdateProvider = shoppingCartUpdateProvider;
  }

  @BeforeEach
  public void setup() {
    if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
      customersPersistenceProvider.add(
          CustomerTestDataBuilder.existingCustomer()
              .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
              .build());
    }
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void shouldUpdateItemPriceAndTotalAmount() {
    ShoppingCart shoppingCart = ShoppingCart.startShopping(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

    Product product1 = ProductTestDataBuilder.aProduct().price(new Money("2000")).build();
    Product product2 = ProductTestDataBuilder.aProductAltRamMemory().price(new Money("200")).build();

    shoppingCart.addItem(product1, new Quantity(2));
    shoppingCart.addItem(product2, new Quantity(1));

    persistenceProvider.add(shoppingCart);

    ProductId productIdToUpdate = product1.id();
    Money newProduct1Price = new Money("1500");
    Money expectedNewItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
    Money expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(new Money("200"));

    shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

    ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

    Assertions.assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);
    Assertions.assertThat(updatedShoppingCart.totalItems()).isEqualTo(new Quantity(3));

    ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

    Assertions.assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
    Assertions.assertThat(item.price()).isEqualTo(newProduct1Price);

  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void shouldUpdateItemAvailability() {
    ShoppingCart shoppingCart = ShoppingCart.startShopping(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

    Product product1 = ProductTestDataBuilder.aProduct()
        .price(new Money("2000"))
        .inStock(true).build();
    Product product2 = ProductTestDataBuilder.aProductAltRamMemory()
        .price(new Money("200"))
        .inStock(true).build();

    shoppingCart.addItem(product1, new Quantity(2));
    shoppingCart.addItem(product2, new Quantity(1));

    persistenceProvider.add(shoppingCart);

    var productIdToUpdate = product1.id();
    var productIdNotToUpdate = product2.id();

    shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

    ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

    ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

    Assertions.assertThat(item.isAvailable()).isFalse();

    ShoppingCartItem item2 = updatedShoppingCart.findItem(productIdNotToUpdate);

    Assertions.assertThat(item2.isAvailable()).isTrue();

  }

}
