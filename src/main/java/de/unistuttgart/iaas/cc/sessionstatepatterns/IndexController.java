package de.unistuttgart.iaas.cc.sessionstatepatterns;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

	@RequestMapping("/")
	public String hello(Model model) {

		List<String> shoppingCart = ShoppingCartDaoLocal.getShoppingCart().getAllShoppingCartItems();
		
		model.addAttribute("cart", shoppingCart);
		return "index";
	}
}
