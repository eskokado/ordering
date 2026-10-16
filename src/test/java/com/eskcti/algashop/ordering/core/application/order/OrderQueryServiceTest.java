package com.eskcti.algashop.ordering.core.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;

import com.eskcti.algashop.ordering.core.application.security.SecurityCheckApplicationService;
import com.eskcti.algashop.ordering.core.ports.in.order.OrderFilter;
import com.eskcti.algashop.ordering.core.ports.out.order.CustomerMinimalOutput;
import com.eskcti.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.eskcti.algashop.ordering.core.ports.out.order.OrderSummaryOutput;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderQueryServiceTest {

    @Mock
    private ForObtainingOrders forObtainingOrders;

    @Mock
    private SecurityCheckApplicationService securityCheck;

    @InjectMocks
    private OrderQueryService orderQueryService;

    private OrderDetailOutput orderOf(UUID customerId) {
        return OrderDetailOutput.builder()
                .id("1")
                .customer(CustomerMinimalOutput.builder().id(customerId).build())
                .build();
    }

    @Test
    void findById_returnsOrder_whenAuthenticatedNonCustomer() {
        UUID ownerId = UUID.randomUUID();
        OrderDetailOutput order = orderOf(ownerId);
        when(forObtainingOrders.findById("1")).thenReturn(order);
        when(securityCheck.isCustomer()).thenReturn(false);
        when(securityCheck.isAuthenticated()).thenReturn(true);

        OrderDetailOutput result = orderQueryService.findById("1");

        assertThat(result).isSameAs(order);
    }

    @Test
    void findById_returnsOrder_whenCustomerIsOwner() {
        UUID ownerId = UUID.randomUUID();
        OrderDetailOutput order = orderOf(ownerId);
        when(forObtainingOrders.findById("1")).thenReturn(order);
        when(securityCheck.isCustomer()).thenReturn(true);
        when(securityCheck.getAuthenticatedUserId()).thenReturn(ownerId);

        OrderDetailOutput result = orderQueryService.findById("1");

        assertThat(result).isSameAs(order);
    }

    @Test
    void findById_throwsAccessDenied_whenCustomerIsNotOwner() {
        OrderDetailOutput order = orderOf(UUID.randomUUID());
        when(forObtainingOrders.findById("1")).thenReturn(order);
        when(securityCheck.isCustomer()).thenReturn(true);
        when(securityCheck.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> orderQueryService.findById("1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void findById_throwsAccessDenied_whenUnauthenticatedNonCustomer() {
        OrderDetailOutput order = orderOf(UUID.randomUUID());
        when(forObtainingOrders.findById("1")).thenReturn(order);
        when(securityCheck.isCustomer()).thenReturn(false);
        when(securityCheck.isAuthenticated()).thenReturn(false);

        assertThatThrownBy(() -> orderQueryService.findById("1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void filter_setsCustomerId_whenCustomer() {
        UUID customerId = UUID.randomUUID();
        OrderFilter filter = new OrderFilter(10, 0);
        when(securityCheck.isCustomer()).thenReturn(true);
        when(securityCheck.getAuthenticatedUserId()).thenReturn(customerId);
        when(forObtainingOrders.filter(filter)).thenReturn(Page.empty());

        Page<OrderSummaryOutput> result = orderQueryService.filter(filter);

        assertThat(result).isEmpty();
        assertThat(filter.getCustomerId()).isEqualTo(customerId);
        verify(forObtainingOrders).filter(filter);
    }

    @Test
    void filter_doesNotSetCustomerId_whenNonCustomer() {
        OrderFilter filter = new OrderFilter(10, 0);
        when(securityCheck.isCustomer()).thenReturn(false);
        when(forObtainingOrders.filter(filter)).thenReturn(new PageImpl<>(java.util.List.of()));

        Page<OrderSummaryOutput> result = orderQueryService.filter(filter);

        assertThat(filter.getCustomerId()).isNull();
        assertThat(result).isEmpty();
        verify(securityCheck, never()).getAuthenticatedUserId();
    }
}
