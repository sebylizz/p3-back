package dk.leghetto.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dk.leghetto.classes.Cart;
import dk.leghetto.classes.ProductVariantDTO;
import dk.leghetto.classes.ProductVariantRepository;

class CartServiceTest {

    @Mock
    private ProductVariantRepository pvr;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCartFromStrings_ValidData() {
        String[] productIds = {"000/123/456", "cart/789/101"};

        ProductVariantDTO product1 = new ProductVariantDTO();
        product1.setId(456L);
        ProductVariantDTO product2 = new ProductVariantDTO();
        product2.setId(101L);

        when(pvr.getDTO(456L)).thenReturn(product1);
        when(pvr.getDTO(101L)).thenReturn(product2);

        Cart cart = cartService.cartFromStrings(productIds);

        assertNotNull(cart);
        assertEquals(2, cart.getItems().size());
        assertTrue(cart.getItems().contains(product1));
        assertTrue(cart.getItems().contains(product2));

        verify(pvr, times(1)).getDTO(456L);
        verify(pvr, times(1)).getDTO(101L);
    }

    @Test
    void testCartFromStrings_InvalidProductId() {
        String[] productIds = {"000/123/9999"};

        when(pvr.getDTO(9999L)).thenReturn(null);

        Cart cart = cartService.cartFromStrings(productIds);

        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());

        verify(pvr, times(1)).getDTO(9999L);
    }

    @Test
    void testCartFromStrings_EmptyArray() {
        String[] productIds = {};

        Cart cart = cartService.cartFromStrings(productIds);

        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }
}

