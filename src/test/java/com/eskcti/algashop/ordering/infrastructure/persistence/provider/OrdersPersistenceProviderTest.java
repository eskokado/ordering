package com.eskcti.algashop.ordering.infrastructure.persistence.provider;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.eskcti.algashop.ordering.domain.model.order.Order;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.eskcti.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrdersPersistenceProviderTest {

    @Test
    void shouldUpdateOrderVersionFromPersistenceEntity() throws Exception {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrdersPersistenceProvider provider = new OrdersPersistenceProvider(null, null, null, null);
        Method updateVersion = OrdersPersistenceProvider.class.getDeclaredMethod("updateVersion", Order.class,
                OrderPersistenceEntity.class);
        updateVersion.setAccessible(true);

        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .version(7L)
                .build();

        updateVersion.invoke(provider, order, persistenceEntity);

        assertEquals(7L, order.version());
    }

    @Test
    void shouldPropagateExceptionWhenUpdateVersionFails() throws Exception {
        Order order = new OrderWithoutDeclaredVersion(OrderTestDataBuilder.anOrder().build());
        OrdersPersistenceProvider provider = new OrdersPersistenceProvider(null, null, null, null);
        Method updateVersion = OrdersPersistenceProvider.class.getDeclaredMethod("updateVersion", Order.class,
                OrderPersistenceEntity.class);
        updateVersion.setAccessible(true);

        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .version(7L)
                .build();

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            updateVersion.invoke(provider, order, persistenceEntity);
        });

        assertInstanceOf(NoSuchFieldException.class, exception.getCause());
    }

    private static final class OrderWithoutDeclaredVersion extends Order {

        private OrderWithoutDeclaredVersion(Order source) {
            super(
                    source.id(),
                    source.customerId(),
                    source.version(),
                    source.totalAmount(),
                    source.totalItems(),
                    source.placedAt(),
                    source.paidAt(),
                    source.canceledAt(),
                    source.readyAt(),
                    source.billing(),
                    source.shipping(),
                    source.status(),
                    source.paymentMethod(),
                    Set.copyOf(source.items()));
        }
    }
}
