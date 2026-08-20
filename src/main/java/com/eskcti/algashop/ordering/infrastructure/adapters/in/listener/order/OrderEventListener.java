package com.eskcti.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.eskcti.algashop.ordering.core.domain.model.order.OrderCanceledEvent;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderPaidEvent;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderPlacedEvent;
import com.eskcti.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @EventListener
    public void listen(OrderPlacedEvent event) {

    }

    @EventListener
    public void listen(OrderPaidEvent event) {

    }

    @EventListener
    public void listen(OrderReadyEvent event) {

    }

    @EventListener
    public void listen(OrderCanceledEvent event) {

    }

}
