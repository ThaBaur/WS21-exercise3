package de.unistuttgart.iaas.cc.sessionstatepatterns;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddNewItemToDatabaseCartController {

	@RequestMapping("/set")
	public String hello(@RequestParam(value = "item", required = false, defaultValue = "") String newItem) {

		ShoppingCartDaoLocal.getShoppingCart().addShoppingCartItem(newItem);

		return "set-cookie";
	}
}
