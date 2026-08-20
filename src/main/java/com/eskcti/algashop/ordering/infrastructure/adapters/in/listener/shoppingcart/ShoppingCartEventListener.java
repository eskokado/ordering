package com.eskcti.algashop.ordering.infrastructure.adapters.in.listener.shoppingcart;

import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.eskcti.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartEventListener {

    @EventListener
    public void listen(ShoppingCartCreatedEvent event) {

    }

    @EventListener
    public void listen(ShoppingCartEmptiedEvent event) {

    }

    @EventListener
    public void listen(ShoppingCartItemAddedEvent event) {

    }

    @EventListener
    public void listen(ShoppingCartItemRemovedEvent event) {

    }

}
