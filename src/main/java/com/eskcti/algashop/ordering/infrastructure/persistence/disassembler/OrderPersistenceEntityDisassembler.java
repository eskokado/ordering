package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.domain.model.entity.OrderItem;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.Billing;
import com.eskcti.algashop.ordering.domain.model.valueobject.Document;
import com.eskcti.algashop.ordering.domain.model.valueobject.Email;
import com.eskcti.algashop.ordering.domain.model.valueobject.FullName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Phone;
import com.eskcti.algashop.ordering.domain.model.valueobject.ProductName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.Recipient;
import com.eskcti.algashop.ordering.domain.model.valueobject.Shipping;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
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