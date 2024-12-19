package dk.leghetto.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dk.leghetto.classes.ProductDTO;
import dk.leghetto.classes.ProductGetPostDTO;
import dk.leghetto.classes.ProductRepository;
import dk.leghetto.classes.ProductVariantRepository;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;


public class ProductResourceTest {

    @InjectMocks
    private ProductResource productResource;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        List<ProductDTO> products = List.of(new ProductDTO());
        when(productRepository.getAllActiveProducts()).thenReturn(products);

        Response response = productResource.getAllProducts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(products, response.getEntity());
    }

    @Test
    void testAddProduct_Success() throws Exception {
        ProductGetPostDTO request = new ProductGetPostDTO();
        Map<String, Object> result = Map.of("id", 1L);
        when(productRepository.addProduct(request)).thenReturn(result);

        Response response = productResource.addProduct(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(result, response.getEntity());
    }

    @Test
    void testAddProduct_BadRequest() throws Exception {
        ProductGetPostDTO request = new ProductGetPostDTO();
        when(productRepository.addProduct(request)).thenThrow(new IllegalArgumentException("Invalid data"));

        Response response = productResource.addProduct(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Invalid data", response.getEntity());
    }

    @Test
    void testGetProductWithPricesById_Success() {
        Long productId = 1L;
        ProductGetPostDTO productDTO = new ProductGetPostDTO();
        when(productRepository.getProductWithPricesById(productId)).thenReturn(productDTO);

        Response response = productResource.getProductWithPricesById(productId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(productDTO, response.getEntity());
    }

    @Test
    void testGetProductWithPricesById_NotFound() {
        Long productId = 1L;
        when(productRepository.getProductWithPricesById(productId))
                .thenThrow(new NotFoundException("Product not found"));

        Response response = productResource.getProductWithPricesById(productId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Product not found", response.getEntity());
    }
}
