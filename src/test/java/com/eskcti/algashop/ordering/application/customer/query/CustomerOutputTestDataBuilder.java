package com.eskcti.algashop.ordering.application.customer.query;

import com.eskcti.algashop.ordering.application.commons.AddressData;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerOutputTestDataBuilder {

	public static final UUID DEFAULT_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

	public static final OffsetDateTime DEFAULT_REGISTERED_AT = OffsetDateTime.parse("2025-01-15T10:30:00Z");

	public static CustomerOutput.CustomerOutputBuilder existing() {
		return CustomerOutput.builder()
				.id(DEFAULT_CUSTOMER_ID)
				.registeredAt(DEFAULT_REGISTERED_AT)
				.archivedAt(null)
				.archived(false)
				.phone("1191234564")
				.email("johndoe@email.com")
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(1991, 7, 5))
				.document("12345")
				.promotionNotificationsAllowed(false)
				.loyaltyPoints(0)
				.address(AddressData.builder()
						.street("Bourbon Street")
						.number("2000")
						.complement("apt 122")
						.neighborhood("North Ville")
						.city("Yostfort")
						.state("South Carolina")
						.zipCode("12321")
						.build());
	}

}