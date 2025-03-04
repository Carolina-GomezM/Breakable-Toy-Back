package breakable.toy.breakable_toy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = BreakableToyApplication.class)
@AutoConfigureMockMvc 
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private ProductRepoImp productRepoImp;

    private Product product;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        product = Product.builder()
            .ID(1)
            .name("Banana")
            .category("Fruits")
            .stock(20)
            .price(30.00f)
            .expDate(LocalDate.of(2024, 12, 23))
            .build();

        products = new ArrayList<>();
            products.add(Product.builder().ID(0).name("Banana").category("Fruits").stock(20).price(30.00f).expDate(LocalDate.of(2024, 12, 23)).build());
            products.add(Product.builder().ID(1).name("Apple").category("Fruits").stock(15).price(25.00f).expDate(LocalDate.of(2024, 12, 20)).build());
    }

    @Test
    void testSaveProduct() throws Exception {
        // Mock del repositorio, se simula la respuesta del método addProduct
        Mockito.when(productRepoImp.addProduct(Mockito.any(Product.class))).thenReturn(product);

        
        String requestBody = """
        {
            "name": "Banana",
            "category": "Fruits",
            "stock": 20,
            "price": 30.00,
            "expDate": "2024-12-23"
        }
        """;

        
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk()) 
             .andExpect(jsonPath("$.expDate").value("2024-12-23")); 
    }

    @Test
    void testFindProducts() throws Exception{
        Mockito.when(productRepoImp.findByFilters("Banana", null, null)).thenReturn(products);

        mockMvc.perform(get("/products")
        .param("name", "Banana"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Banana"));
    }

    @Test
    void testFindProductsNoResults() throws Exception{
        Mockito.when(productRepoImp.findByFilters("Banana", new ArrayList<String>(), "In Stock")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products")
        .param("name", "Banana")
        .param("category", "Fruits")
        .param("availability", "In Stock"))
        .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct() throws Exception{

        Product updatedProduct = Product.builder()
        .ID(1)
        .name("Watermelon")
        .category("Fruits")
        .stock(10)
        .price(15.00f)
        .expDate(LocalDate.of(2024, 12, 23))
        .build();



        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.when(productRepoImp.modifyProduct(1, updatedProduct)).thenReturn(updatedProduct);

            String requestBody = """
                {
                    "name": "Watermelon",
                    "category": "Fruits",
                    "stock": 10,
                    "price": 15.00,
                    "expDate": "2024-12-23"
                }
                """;

                mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Watermelon"))
            .andExpect(jsonPath("$.stock").value(10))
            .andExpect(jsonPath("$.price").value(15.00));
    }

    @Test
    void testDeleteProduct() throws Exception{
        Mockito.when(productRepoImp.deleteProduct(1)).thenReturn(true);

        mockMvc.perform(delete("/products/{id}", 1))
        .andExpect(status().isOk());

        Mockito.verify(productRepoImp).deleteProduct(1);
    }

    @Test
    void testPutOutOfStock() throws Exception{
        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.doNothing().when(productRepoImp).outOfStock(1);

        mockMvc.perform(post("/products/{id}/outofstock",1))
        .andExpect(status().isOk());

        Mockito.verify(productRepoImp).searchId(1);
        Mockito.verify(productRepoImp).outOfStock(1);
    }

    @Test
    void testPutInStock() throws Exception{
        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.doNothing().when(productRepoImp).outOfStock(1);

        mockMvc.perform(put("/products/{id}/instock",1))
        .andExpect(status().isOk());

        Mockito.verify(productRepoImp).searchId(1);
    }
}



