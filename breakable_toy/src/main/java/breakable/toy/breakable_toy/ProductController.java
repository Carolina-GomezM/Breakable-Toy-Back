package breakable.toy.breakable_toy;

import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin
@RequestMapping("/products")
public class ProductController {
    private final ProductRepo productRepo;

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        
        this.productRepo.addProduct(product);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String availability) {
            if(name == null && category == null && availability == null){
                List<Product> products = this.productRepo.getAllProducts();
                return ResponseEntity.ok(products);
            }
            List<String> categoryList = (category== null || category.isEmpty() ? null: Arrays.asList(category.split(",")));
            List<Product> products = productRepo.findByFilters(name, categoryList, availability);

            return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product newProduct) {
        Product existingProduct = productRepo.searchId(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        newProduct.setID(id);
        Product updated = productRepo.modifyProduct(id, newProduct);

        if (updated == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delProduct(@PathVariable int id) {
        boolean deleted = productRepo.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/outofstock")
    public ResponseEntity<Void> putOutOfStock(@PathVariable int id) {
        Product existingProduct = productRepo.searchId(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        this.productRepo.outOfStock(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/instock")
    public ResponseEntity<Void> putStock(@PathVariable int id) {
        Product existingProduct = productRepo.searchId(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        this.productRepo.withStock(id);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/categories")
    public ResponseEntity<List<String>> allCategories(){
        List <String> categories = productRepo.obtainCategories();
        if(categories.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Reports>> getMethodName() {
        List<Reports> categorySummaries = productRepo.obtainReports();
        return ResponseEntity.ok(categorySummaries);
    }
    


}
