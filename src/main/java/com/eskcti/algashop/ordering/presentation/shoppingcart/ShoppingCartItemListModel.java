package com.eskcti.algashop.ordering.presentation.shoppingcart;

import com.eskcti.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
