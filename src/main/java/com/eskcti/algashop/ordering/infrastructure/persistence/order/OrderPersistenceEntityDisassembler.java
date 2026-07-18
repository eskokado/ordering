package com.eskcti.algashop.ordering.infrastructure.persistence.order;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.eskcti.algashop.ordering.domain.model.commons.Address;
import com.eskcti.algashop.ordering.domain.model.commons.Document;
import com.eskcti.algashop.ordering.domain.model.commons.Email;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.commons.Money;
import com.eskcti.algashop.ordering.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.domain.model.commons.Quantity;
import com.eskcti.algashop.ordering.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.domain.model.order.Billing;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.domain.model.order.OrderId;
import com.eskcti.algashop.ordering.domain.model.order.OrderItem;
import com.eskcti.algashop.ordering.domain.model.order.OrderItemId;
import com.eskcti.algashop.ordering.domain.model.order.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.order.PaymentMethod;
import com.eskcti.algashop.ordering.domain.model.order.Recipient;
import com.eskcti.algashop.ordering.domain.model.order.Shipping;
import com.eskcti.algashop.ordering.domain.model.product.ProductId;
import com.eskcti.algashop.ordering.domain.model.product.ProductName;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

        public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
                return Order.existing()
                                .id(new OrderId(persistenceEntity.getId()))
                                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                                .version(persistenceEntity.getVersion())
                                .totalAmount(persistenceEntity.getTotalAmount() != null
                                                ? new Money(persistenceEntity.getTotalAmount())
                                                : Money.ZERO)
                                .totalItems(persistenceEntity.getTotalItems() != null
                                                ? new Quantity(persistenceEntity.getTotalItems())
                                                : Quantity.ZERO)
                                .status(persistenceEntity.getStatus() != null
                                                ? OrderStatus.valueOf(persistenceEntity.getStatus())
                                                : OrderStatus.DRAFT)
                                .paymentMethod(persistenceEntity.getPaymentMethod() != null
                                                ? PaymentMethod.valueOf(persistenceEntity.getPaymentMethod())
                                                : null)
                                .placedAt(persistenceEntity.getPlacedAt())
                                .paidAt(persistenceEntity.getPaidAt())
                                .canceledAt(persistenceEntity.getCanceledAt())
                                .readyAt(persistenceEntity.getReadyAt())
                                .billing(toBillingValueObject(persistenceEntity.getBilling()))
                                .shipping(toShippingValueObject(persistenceEntity.getShipping()))
                                .items(toDomainEntity(persistenceEntity.getItems()))
                                .build();
        }

        private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
                if (items == null || items.isEmpty()) {
                        return new HashSet<>();
                }
                return items.stream().map(i -> toDomainEntity(i)).collect(Collectors.toSet());
        }

        private OrderItem toDomainEntity(OrderItemPersistenceEntity persistenceEntity) {
                return OrderItem.existing()
                                .id(new OrderItemId(persistenceEntity.getId()))
                                .orderId(new OrderId(persistenceEntity.getOrderId()))
                                .productId(new ProductId(persistenceEntity.getProductId()))
                                .productName(new ProductName(persistenceEntity.getProductName()))
                                .price(new Money(persistenceEntity.getPrice()))
                                .quantity(new Quantity(persistenceEntity.getQuantity()))
                                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                                .build();
        }

        private Shipping toShippingValueObject(ShippingEmbeddable shippingEmbeddable) {
                if (shippingEmbeddable == null) {
                        return null;
                }
                RecipientEmbeddable recipientEmbeddable = shippingEmbeddable.getRecipient();
                return Shipping.builder()
                                .cost(new Money(shippingEmbeddable.getCost()))
                                .expectedDate(shippingEmbeddable.getExpectedDate())
                                .recipient(Recipient.builder()
                                                .fullName(new FullName(
                                                                recipientEmbeddable.getFirstName(),
                                                                recipientEmbeddable.getLastName()))
                                                .document(new Document(
                                                                recipientEmbeddable.getDocument()))
                                                .phone(new Phone(recipientEmbeddable.getPhone()))
                                                .build())
                                .address(toAddressValueObject(shippingEmbeddable.getAddress()))
                                .build();
        }

        private Billing toBillingValueObject(BillingEmbeddable billingEmbeddable) {
                if (billingEmbeddable == null) {
                        return null;
                }
                return Billing.builder()
                                .fullName(new FullName(billingEmbeddable.getFirstName(),
                                                billingEmbeddable.getLastName()))
                                .document(new Document(billingEmbeddable.getDocument()))
                                .phone(new Phone(billingEmbeddable.getPhone()))
                                .email(new Email(billingEmbeddable.getEmail()))
                                .address(toAddressValueObject(billingEmbeddable.getAddress()))
                                .build();
        }

        private Address toAddressValueObject(AddressEmbeddable address) {
                return Address.builder()
                                .street(address.getStreet())
                                .number(address.getNumber())
                                .complement(address.getComplement())
                                .neighborhood(address.getNeighborhood())
                                .city(address.getCity())
                                .state(address.getState())
                                .zipCode(new ZipCode(address.getZipCode()))
                                .build();
        }

}