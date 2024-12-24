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
@AutoConfigureMockMvc // Asegúrate de incluir esta anotación para configurar MockMvc automáticamente
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;  // Spring Boot lo inyecta automáticamente

    @MockBean
    private ProductRepoImp productRepoImp;

    private Product product;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        product = Product.builder()
            .id(1)
            .name("Banana")
            .category("Fruits")
            .stock(20)
            .price(30.00f)
            .exp_date(LocalDate.of(2024, 12, 23))
            .build();

        products = new ArrayList<>();
            products.add(Product.builder().id(0).name("Banana").category("Fruits").stock(20).price(30.00f).exp_date(LocalDate.of(2024, 12, 23)).build());
            products.add(Product.builder().id(1).name("Apple").category("Fruits").stock(15).price(25.00f).exp_date(LocalDate.of(2024, 12, 20)).build());
    }

    @Test
    void testSaveProduct() throws Exception {
        // Mock del repositorio, se simula la respuesta del método addProduct
        Mockito.when(productRepoImp.addProduct(Mockito.any(Product.class))).thenReturn(product);

        // Crear el JSON para enviar
        String requestBody = """
        {
            "name": "Banana",
            "category": "Fruits",
            "stock": 20,
            "price": 30.00,
            "exp_date": "2024-12-23"
        }
        """;

        // Realizar la solicitud POST usando MockMvc
        mockMvc.perform(post("/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk()); // Verifica que la respuesta tenga el código de estado 200 OK
    }

    @Test
    void testFindProducts() throws Exception{
        Mockito.when(productRepoImp.findByFilters("Banana", null, null)).thenReturn(products);

        mockMvc.perform(get("/product/search")
        .param("name", "Banana"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Banana"));
    }

    @Test
    void testFindProductsNoResults() throws Exception{
        Mockito.when(productRepoImp.findByFilters("Banana", "Fruits", "In Stock")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/product/search")
        .param("name", "Banana")
        .param("category", "Fruits")
        .param("availability", "In Stock"))
        .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct() throws Exception{

        Product updatedProduct = Product.builder()
        .id(1)
        .name("Watermelon")
        .category("Fruits")
        .stock(10)
        .price(15.00f)
        .exp_date(LocalDate.of(2024, 12, 23))
        .build();



        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.when(productRepoImp.modifyProduct(1, updatedProduct)).thenReturn(updatedProduct);

            String requestBody = """
                {
                    "name": "Watermelon",
                    "category": "Fruits",
                    "stock": 10,
                    "price": 15.00,
                    "exp_date": "2024-12-23"
                }
                """;

                mockMvc.perform(put("/product/update/{id}", 1)
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

        mockMvc.perform(delete("/product/delete/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().string("The product has been removed."));

        Mockito.verify(productRepoImp).deleteProduct(1);
    }

    @Test
    void testPutOutOfStock() throws Exception{
        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.doNothing().when(productRepoImp).outOfStock(1);

        mockMvc.perform(put("/product/outOfStock/{id}",1))
        .andExpect(status().isOk())
        .andExpect(content().string("Stock in 0"));

        Mockito.verify(productRepoImp).searchId(1);
        Mockito.verify(productRepoImp).outOfStock(1);
    }

    @Test
    void testPutInStock() throws Exception{
        Mockito.when(productRepoImp.searchId(1)).thenReturn(product);
        Mockito.doNothing().when(productRepoImp).outOfStock(1);

        mockMvc.perform(put("/product/withStock/{id}",1))
        .andExpect(status().isOk())
        .andExpect(content().string("Stock in 10"));

        Mockito.verify(productRepoImp).searchId(1);
        Mockito.verify(productRepoImp).outOfStock(1);
    }
}



