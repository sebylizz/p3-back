package dk.leghetto.services;

import dk.leghetto.classes.Cart;
import dk.leghetto.classes.ProductVariantDTO;
import dk.leghetto.classes.ProductVariantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CartService {
    @Inject
    ProductVariantRepository pvr;

    public Cart cartFromStrings(String[] s) {
        Cart c = new Cart();
        for (String i : s) {
            ProductVariantDTO p = pvr.getDTO(Long.parseLong(i.split("/")[2]));
            if(p != null)
                c.addProduct(p);
        }

        return c;
    }
}
