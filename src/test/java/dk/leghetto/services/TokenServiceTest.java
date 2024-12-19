package dk.leghetto.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import jakarta.inject.Inject;

class TokenServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Inject
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService();
        tokenService.cr = customerRepository;
    }

    @Test
    void testGenerateToken_UserRole() {
        String email = "user@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setRole("user");
        
        when(customerRepository.findByEmail(email)).thenReturn(customer);

        String token = tokenService.generateToken(email);

        assertNotNull(token);
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGenerateToken_AdminRole() {
        String email = "admin@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setRole("admin");

        when(customerRepository.findByEmail(email)).thenReturn(customer);

        String token = tokenService.generateToken(email);

        assertNotNull(token);
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetRoles_User() {
        HashSet<String> roles = tokenService.getRoles("user");

        assertEquals(1, roles.size());
        assertTrue(roles.contains("user"));
        assertFalse(roles.contains("admin"));
    }

    @Test
    void testGetRoles_Admin() {
        HashSet<String> roles = tokenService.getRoles("admin");

        assertEquals(2, roles.size());
        assertTrue(roles.contains("user"));
        assertTrue(roles.contains("admin"));
    }

    @Test
    void testGenerateToken_NullCustomer() {
        String email = "unknown@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> tokenService.generateToken(email));
        verify(customerRepository, times(1)).findByEmail(email);
    }
}
