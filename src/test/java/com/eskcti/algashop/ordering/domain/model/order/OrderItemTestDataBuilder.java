package com.eskcti.algashop.ordering.domain.model.order;

import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderItem;
import com.eskcti.algashop.ordering.domain.model.order.OrderItemId;
import com.eskcti.algashop.ordering.domain.model.product.Product;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;

public final class OrderItemTestDataBuilder {

  private OrderItemTestDataBuilder() {
  }

  public static OrderItem.ExistingOrderItemBuilder existingOrderItem(OrderId orderId) {
    return OrderItem.existing()
        .id(new OrderItemId(1L))
        .orderId(orderId)
        .productId(validProductId())
        .productName(validProductName())
        .price(validPrice())
        .quantity(validQuantity())
        .totalAmount(new Money("100.00"));
  }

  public static OrderItem.BrandNewOrderItemBuilder brandNewOrderItem(OrderId orderId) {
    return OrderItem.brandNew()
        .orderId(orderId)
        .product(validProduct())
        .quantity(validQuantity());
  }

  public static Product validProduct() {
    return Product.builder()
        .id(validProductId())
        .name(validProductName())
        .price(validPrice())
        .inStock(true)
        .build();
  }

  public static Product productWith(ProductName name, Money price) {
    return Product.builder()
        .id(new ProductId())
        .name(name)
        .price(price)
        .inStock(true)
        .build();
  }

  public static ProductId validProductId() {
    return new ProductId();
  }

  public static ProductName validProductName() {
    return new ProductName("Notebook");
  }

  public static Money validPrice() {
    return new Money("50.00");
  }

  public static Quantity validQuantity() {
    return new Quantity(2);
  }
}
