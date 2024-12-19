package breakable.toy.breakable_toy;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductRepo productRepo;

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        this.productRepo.addProduct(product);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = this.productRepo.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product newProduct) {
        Product existingProduct = productRepo.searchId(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        newProduct.setID(id);
        Product updated = productRepo.modifyProduct(id, newProduct);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delProduct(@PathVariable int id) {
        boolean deleted = productRepo.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.ok("The product has been removed.");
        } else {
            return ResponseEntity.status(404).body("The product does not exists.");
        }
    }

    @PutMapping("/outOfStock/{id}")
    public ResponseEntity<String> putOutOfStock(@PathVariable int id) {
        Product existingProduct = productRepo.searchId(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        this.productRepo.outOfStock(id);
        return ResponseEntity.ok("Stock in 0");
    }

    @PutMapping("/withStock/{id}")
    public ResponseEntity<String> putStock(@PathVariable int id) {
        Product existingProduct = productRepo.searchId(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        this.productRepo.outOfStock(id);
        return ResponseEntity.ok("Stock with 10");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> findProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String availability) {
        List<Product> products = productRepo.findByFilters(name, category, availability);
        if (products.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

}
