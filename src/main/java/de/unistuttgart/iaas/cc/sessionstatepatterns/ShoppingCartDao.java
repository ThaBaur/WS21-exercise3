package de.unistuttgart.iaas.cc.sessionstatepatterns;

import java.util.List;

public interface ShoppingCartDao {
    public List<String> getAllShoppingCartItems();
    public void addShoppingCartItem(String item);
}
