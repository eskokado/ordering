package com.eskcti.algashop.ordering.infrastructure.listener.order;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.eskcti.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.eskcti.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.eskcti.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.eskcti.algashop.ordering.domain.model.order.OrderReadyEvent;

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