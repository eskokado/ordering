package com.eskcti.algashop.ordering.domain.model.shoppingcart;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;
import com.eskcti.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.eskcti.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemIncompatibleProductException;

import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartItemTest {

        @Test
        void given_brandNewShoppingCartItem_whenBuild_shouldInitializeDefaults() {
                ShoppingCartId shoppingCartId = new ShoppingCartId();
                ProductId productId = ShoppingCartItemTestDataBuilder.validProductId();
                ProductName productName = ShoppingCartItemTestDataBuilder.validProductName();
                Money price = ShoppingCartItemTestDataBuilder.validPrice();

                ShoppingCartItem cartItem = ShoppingCartItem.brandNew()
                                .shoppingCartId(shoppingCartId)
                                .productId(productId)
                                .productName(productName)
                                .price(price)
                                .available(true)
                                .quantity(ShoppingCartItemTestDataBuilder.validQuantity())
                                .build();

                assertThat(cartItem.id()).isNotNull();
                assertThat(cartItem.shoppingCartId()).isEqualTo(shoppingCartId);
                assertThat(cartItem.productId()).isEqualTo(productId);
                assertThat(cartItem.name()).isEqualTo(productName);
                assertThat(cartItem.price()).isEqualTo(price);
                assertThat(cartItem.quantity()).isEqualTo(ShoppingCartItemTestDataBuilder.validQuantity());
                assertThat(cartItem.totalAmount()).isEqualTo(new Money("100.00"));
        }

        @Test
        void given_brandNewShoppingCartItem_whenBuild_shouldCalculateTotalFromPriceAndQuantity() {
                ShoppingCartId shoppingCartId = new ShoppingCartId();
                Product product = Product.builder()
                                .id(new ProductId())
                                .name(ShoppingCartItemTestDataBuilder.validProductName())
                                .price(new Money("25.50"))
                                .inStock(true)
                                .build();
                ShoppingCartItem cartItem = ShoppingCartItem.brandNew()
                                .shoppingCartId(shoppingCartId)
                                .productId(product.id())
                                .productName(product.name())
                                .price(product.price())
                                .available(product.inStock())
                                .quantity(new Quantity(3))
                                .build();

                assertThat(cartItem.totalAmount()).isEqualTo(new Money("76.50"));
        }

        @Test
        void given_existingShoppingCartItem_whenBuild_shouldExposeFields() {
                ShoppingCartId shoppingCartId = new ShoppingCartId();
                ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.existingItem(shoppingCartId).build();

                assertThat(cartItem.id()).isNotNull();
                assertThat(cartItem.shoppingCartId()).isEqualTo(shoppingCartId);
                assertThat(cartItem.productId()).isNotNull();
                assertThat(cartItem.name()).isEqualTo(new ProductName("Notebook"));
                assertThat(cartItem.price()).isEqualTo(new Money("50.00"));
                assertThat(cartItem.quantity()).isEqualTo(new Quantity(2));
                assertThat(cartItem.totalAmount()).isEqualTo(new Money("100.00"));
        }

        @Test
        void given_shoppingCartItemsWithSameId_whenCompare_shouldBeEqual() {
                ShoppingCartItemId cartItemId = new ShoppingCartItemId();
                ShoppingCartId cartId = new ShoppingCartId();
                ShoppingCartItem first = ShoppingCartItemTestDataBuilder.existingItem(cartId).id(cartItemId).build();
                ShoppingCartItem second = ShoppingCartItemTestDataBuilder.existingItem(cartId).id(cartItemId).build();

                assertThat(first).isEqualTo(second);
                assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void given_shoppingCartItemsWithDifferentId_whenCompare_shouldNotBeEqual() {
                ShoppingCartId cartId = new ShoppingCartId();
                ShoppingCartItem first = ShoppingCartItemTestDataBuilder.existingItem(cartId)
                                .id(new ShoppingCartItemId())
                                .build();
                ShoppingCartItem second = ShoppingCartItemTestDataBuilder.existingItem(cartId)
                                .id(new ShoppingCartItemId())
                                .build();

                assertThat(first).isNotEqualTo(second);
                assertThat(first).isNotEqualTo(null);
                assertThat(first).isNotEqualTo("not-a-shopping-cart-item");
        }

        @Test
        void given_nullRequiredFields_whenBuildExistingShoppingCartItem_shouldGenerateException() {
                ShoppingCartId cartId = new ShoppingCartId();

                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).id(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId)
                                                .shoppingCartId(null).build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).productId(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).productName(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).price(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).quantity(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).available(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.existingItem(cartId).totalAmount(null)
                                                .build());
        }

        @Test
        void given_nullRequiredFields_whenBuildBrandNewShoppingCartItem_shouldGenerateException() {
                ShoppingCartId cartId = new ShoppingCartId();

                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId)
                                                .shoppingCartId(null).build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId).productId(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId).productName(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId).price(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId).available(null)
                                                .build());
                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> ShoppingCartItemTestDataBuilder.brandNewItem(cartId).quantity(null)
                                                .build());
        }

        @Test
        void given_shoppingCartItem_whenChangeQuantity_shouldUpdateQuantityAndTotalAmount() {
                ShoppingCartId cartId = new ShoppingCartId();
                Product product = Product.builder()
                                .id(new ProductId())
                                .name(new ProductName("Item"))
                                .price(new Money("25.00"))
                                .inStock(true)
                                .build();
                ShoppingCartItem cartItem = ShoppingCartItem.brandNew()
                                .shoppingCartId(cartId)
                                .productId(product.id())
                                .productName(product.name())
                                .price(product.price())
                                .available(product.inStock())
                                .quantity(new Quantity(2))
                                .build();

                assertThat(cartItem.totalAmount()).isEqualTo(new Money("50.00"));

                cartItem.changeQuantity(new Quantity(4));

                assertThat(cartItem.quantity()).isEqualTo(new Quantity(4));
                assertThat(cartItem.totalAmount()).isEqualTo(new Money("100.00"));
        }

        @Test
        void given_shoppingCartItem_whenChangeQuantityWithZero_shouldGenerateException() {
                ShoppingCartId cartId = new ShoppingCartId();
                ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.brandNewItem(cartId).build();

                Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                                .isThrownBy(() -> cartItem.changeQuantity(Quantity.ZERO));
        }

        @Test
        void given_shoppingCartItem_whenRefreshWithCompatibleProduct_shouldUpdateFields() {
                ShoppingCartId cartId = new ShoppingCartId();
                Product originalProduct = ProductTestDataBuilder.aProduct().build();
                ShoppingCartItem cartItem = ShoppingCartItem.brandNew()
                                .shoppingCartId(cartId)
                                .productId(originalProduct.id())
                                .productName(originalProduct.name())
                                .price(originalProduct.price())
                                .available(originalProduct.inStock())
                                .quantity(new Quantity(1))
                                .build();

                Product newProduct = Product.builder()
                                .id(originalProduct.id())
                                .name(new ProductName("Updated Notebook"))
                                .price(new Money("60.00"))
                                .inStock(false)
                                .build();

                cartItem.refresh(newProduct);

                assertThat(cartItem.name()).isEqualTo(newProduct.name());
                assertThat(cartItem.price()).isEqualTo(newProduct.price());
                assertThat(cartItem.isAvailable()).isEqualTo(newProduct.inStock());
                assertThat(cartItem.totalAmount()).isEqualTo(new Money("60.00"));
        }

        @Test
        void given_shoppingCartItem_whenRefreshWithIncompatibleProduct_shouldGenerateException() {
                ShoppingCartId cartId = new ShoppingCartId();
                Product product = ProductTestDataBuilder.aProduct().build();
                ShoppingCartItem cartItem = ShoppingCartItem.brandNew()
                                .shoppingCartId(cartId)
                                .productId(product.id())
                                .productName(product.name())
                                .price(product.price())
                                .available(product.inStock())
                                .quantity(new Quantity(1))
                                .build();

                Product otherProduct = ProductTestDataBuilder.aProductAltRamMemory().build();

                Assertions.assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                                .isThrownBy(() -> cartItem.refresh(otherProduct));
        }

        @Test
        void given_shoppingCartItem_whenRefreshWithNullProduct_shouldGenerateException() {
                ShoppingCartId cartId = new ShoppingCartId();
                ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.brandNewItem(cartId).build();

                Assertions.assertThatNullPointerException()
                                .isThrownBy(() -> cartItem.refresh(null));
        }
}
