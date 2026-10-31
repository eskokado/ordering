package com.eskcti.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.eskcti.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.eskcti.algashop.ordering.infrastructure.config.security.SecurityAnnotations.*;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

	private final ForQueryingShoppingCarts forQueryingShoppingCarts;

	@GetMapping("/{shoppingCartId}")
	@CanReadShoppingCarts
	public ShoppingCartOutput getById(@PathVariable UUID shoppingCartId) {
		return forQueryingShoppingCarts.findById(shoppingCartId);
	}

	@GetMapping("/{shoppingCartId}/items")
	@CanReadShoppingCarts
	public ShoppingCartItemListModel getItems(@PathVariable UUID shoppingCartId) {
		var items = forQueryingShoppingCarts.findById(shoppingCartId).getItems();
		var itemList = new ShoppingCartItemListModel();
		itemList.getItems().addAll(items);
		return itemList;
	}
}