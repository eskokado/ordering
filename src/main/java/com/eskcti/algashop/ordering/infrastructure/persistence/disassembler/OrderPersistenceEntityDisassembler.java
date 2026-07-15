package com.eskcti.algashop.ordering.infrastructure.persistence.disassembler;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.entity.Order;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.eskcti.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.eskcti.algashop.ordering.domain.model.valueobject.Address;
import com.eskcti.algashop.ordering.domain.model.valueobject.Billing;
import com.eskcti.algashop.ordering.domain.model.valueobject.Document;
import com.eskcti.algashop.ordering.domain.model.valueobject.Email;
import com.eskcti.algashop.ordering.domain.model.valueobject.FullName;
import com.eskcti.algashop.ordering.domain.model.valueobject.Money;
import com.eskcti.algashop.ordering.domain.model.valueobject.Phone;
import com.eskcti.algashop.ordering.domain.model.valueobject.Quantity;
import com.eskcti.algashop.ordering.domain.model.valueobject.Recipient;
import com.eskcti.algashop.ordering.domain.model.valueobject.Shipping;
import com.eskcti.algashop.ordering.domain.model.valueobject.ZipCode;
import com.eskcti.algashop.ordering.domain.model.entity.OrderStatus;
import com.eskcti.algashop.ordering.domain.model.entity.PaymentMethod;
import java.util.HashSet;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .version(persistenceEntity.getVersion())
                .totalAmount(persistenceEntity.getTotalAmount() != null ? new Money(persistenceEntity.getTotalAmount())
                        : Money.ZERO)
                .totalItems(persistenceEntity.getTotalItems() != null ? new Quantity(persistenceEntity.getTotalItems())
                        : Quantity.ZERO)
                .status(persistenceEntity.getStatus() != null ? OrderStatus.valueOf(persistenceEntity.getStatus())
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
                .items(new HashSet<>())
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
                .recipient(recipientEmbeddable != null ? Recipient.builder()
                        .fullName(new FullName(recipientEmbeddable.getFirstName(),
                                recipientEmbeddable.getLastName()))
                        .document(new Document(recipientEmbeddable.getDocument()))
                        .phone(new Phone(recipientEmbeddable.getPhone()))
                        .build() : null)
                .address(toAddressValueObject(shippingEmbeddable.getAddress()))
                .build();
    }

    private Billing toBillingValueObject(BillingEmbeddable billingEmbeddable) {
        if (billingEmbeddable == null) {
            return null;
        }
        return Billing.builder()
                .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(new Document(billingEmbeddable.getDocument()))
                .phone(new Phone(billingEmbeddable.getPhone()))
                .email(new Email(billingEmbeddable.getEmail()))
                .address(toAddressValueObject(billingEmbeddable.getAddress()))
                .build();
    }

    private Address toAddressValueObject(AddressEmbeddable address) {
        if (address == null) {
            return null;
        }
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