package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemOutput;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
