package com.eskcti.algashop.ordering.presentation.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.eskcti.algashop.ordering.core.application.shoppingcart.query.ShoppingCartItemOutput;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
