package com.eskcti.algashop.ordering.core.application.checkout;

import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.core.application.commons.AddressData;
import com.eskcti.algashop.ordering.core.domain.model.commons.Address;
import com.eskcti.algashop.ordering.core.domain.model.commons.Document;
import com.eskcti.algashop.ordering.core.domain.model.commons.Email;
import com.eskcti.algashop.ordering.core.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.core.domain.model.commons.Phone;
import com.eskcti.algashop.ordering.core.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.core.domain.model.order.Billing;

@Component
class BillingInputDisassembler {

  public Billing toDomainModel(BillingData billingData) {
    AddressData address = billingData.getAddress();
    return Billing.builder()
        .fullName(new FullName(billingData.getFirstName(), billingData.getLastName()))
        .document(new Document(billingData.getDocument()))
        .phone(new Phone(billingData.getPhone()))
        .email(new Email(billingData.getEmail()))
        .address(Address.builder()
            .street(address.getStreet())
            .number(address.getNumber())
            .complement(address.getComplement())
            .neighborhood(address.getNeighborhood())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(new ZipCode(address.getZipCode()))
            .build())
        .build();
  }
}
