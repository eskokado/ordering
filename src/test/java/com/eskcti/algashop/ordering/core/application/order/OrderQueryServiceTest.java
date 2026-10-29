package com.eskcti.algashop.ordering.core.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.eskcti.algashop.ordering.core.ports.in.order.OrderFilter;
import com.eskcti.algashop.ordering.core.ports.out.order.CustomerMinimalOutput;
import com.eskcti.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderSummaryOutput;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private ForObtainingOrders forObtainingOrders;

    @InjectMocks
    private OrderQueryService orderQueryService;

    private OrderDetailOutput orderOf(UUID customerId) {
        return OrderDetailOutput.builder()
                .id("1")
                .customer(CustomerMinimalOutput.builder().id(customerId).build())
                .build();
    }

    @Test
    void findById_delegatesToForObtainingOrders() {
        OrderDetailOutput order = orderOf(UUID.randomUUID());
        when(forObtainingOrders.findById("1")).thenReturn(order);

        OrderDetailOutput result = orderQueryService.findById("1");

        assertThat(result).isSameAs(order);
        verify(forObtainingOrders).findById("1");
    }

    @Test
    void findByIdAndCustomerId_delegatesToForObtainingOrders() {
        UUID customerId = UUID.randomUUID();
        OrderDetailOutput order = orderOf(customerId);
        when(forObtainingOrders.findByIdAndCustomerId("1", customerId)).thenReturn(order);

        OrderDetailOutput result = orderQueryService.findByIdAndCustomerId("1", customerId);

        assertThat(result).isSameAs(order);
        verify(forObtainingOrders).findByIdAndCustomerId("1", customerId);
    }

    @Test
    void filter_delegatesToForObtainingOrders() {
        OrderFilter filter = new OrderFilter(10, 0);
        Page<OrderSummaryOutput> page = new PageImpl<>(List.of());
        when(forObtainingOrders.filter(filter)).thenReturn(page);

        Page<OrderSummaryOutput> result = orderQueryService.filter(filter);

        assertThat(result).isSameAs(page);
        verify(forObtainingOrders).filter(filter);
    }
}
