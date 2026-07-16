package com.eskcti.algashop.ordering.infrastructure.persistence.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.entity.Customer;
import com.eskcti.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;

class CustomersPersistenceProviderTest {

    @Test
    void shouldUpdateCustomerVersionFromPersistenceEntity() throws Exception {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomersPersistenceProvider provider = new CustomersPersistenceProvider(null, null, null, null);
        Method updateVersion = CustomersPersistenceProvider.class.getDeclaredMethod("updateVersion", Customer.class,
                CustomerPersistenceEntity.class);
        updateVersion.setAccessible(true);

        CustomerPersistenceEntity persistenceEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer()
                .version(7L)
                .build();

        updateVersion.invoke(provider, customer, persistenceEntity);

        assertEquals(7L, customer.version());
    }

    @Test
    void shouldPropagateExceptionWhenUpdateVersionFails() throws Exception {
        Customer customer = new CustomerWithoutDeclaredVersion(CustomerTestDataBuilder.brandNewCustomer().build());
        CustomersPersistenceProvider provider = new CustomersPersistenceProvider(null, null, null, null);
        Method updateVersion = CustomersPersistenceProvider.class.getDeclaredMethod("updateVersion", Customer.class,
                CustomerPersistenceEntity.class);
        updateVersion.setAccessible(true);

        CustomerPersistenceEntity persistenceEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer()
                .version(7L)
                .build();

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            updateVersion.invoke(provider, customer, persistenceEntity);
        });

        assertInstanceOf(NoSuchFieldException.class, exception.getCause());
    }

    private static final class CustomerWithoutDeclaredVersion extends Customer {
        private CustomerWithoutDeclaredVersion(Customer source) {
            super(
                    source.id(),
                    source.version(),
                    source.fullName(),
                    source.birthDate(),
                    source.email(),
                    source.phone(),
                    source.document(),
                    source.isPromotionNotificationsAllowed(),
                    source.isArchived(),
                    source.registeredAt(),
                    source.archivedAt(),
                    source.loyaltyPoints(),
                    source.address());
        }
    }
}
