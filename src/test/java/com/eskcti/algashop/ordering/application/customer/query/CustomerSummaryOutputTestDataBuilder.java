package com.eskcti.algashop.ordering.application.customer.query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerSummaryOutputTestDataBuilder {

	public static final UUID DEFAULT_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

	public static final UUID SECOND_CUSTOMER_ID = UUID.fromString("7f259ce6-58a7-4133-b0eb-18dfbb305e8b");

	public static final OffsetDateTime DEFAULT_REGISTERED_AT = OffsetDateTime.parse("2025-01-15T10:30:00Z");

	public static final OffsetDateTime SECOND_REGISTERED_AT = OffsetDateTime.parse("2025-02-20T14:45:00Z");

	public static CustomerSummaryOutput.CustomerSummaryOutputBuilder existing() {
		return CustomerSummaryOutput.builder()
				.id(DEFAULT_CUSTOMER_ID)
				.registeredAt(DEFAULT_REGISTERED_AT)
				.archivedAt(null)
				.phone("1191234564")
				.email("johndoe@email.com")
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(1991, 7, 5))
				.document("12345")
				.promotionNotificationsAllowed(false)
				.loyaltyPoints(0)
				.archived(false);
	}

	public static CustomerSummaryOutput.CustomerSummaryOutputBuilder existingAlt1() {
		return CustomerSummaryOutput.builder()
				.id(SECOND_CUSTOMER_ID)
				.registeredAt(SECOND_REGISTERED_AT)
				.archivedAt(null)
				.phone("119123456")
				.email("scott1977@email.com")
				.firstName("Scott")
				.lastName("Stacey")
				.birthDate(LocalDate.of(1977, 1, 5))
				.document("98745")
				.promotionNotificationsAllowed(true)
				.loyaltyPoints(10)
				.archived(false);
	}

}