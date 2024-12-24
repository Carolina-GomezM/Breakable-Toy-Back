package breakable.toy.breakable_toy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ProductRepoImp implements ProductRepo {

    private ArrayList<Product> productStorage = new ArrayList<>();
    private AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public Product addProduct(Product product) {
        product.setID(idCounter.getAndIncrement());
        product.setCreationDate(LocalDate.now());
        product.setupdDate(null);
        this.productStorage.add(product);
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(this.productStorage);
    }

    @Override
    public Product searchId(int id) {
        return this.productStorage.stream().filter(product -> product.getID() == id).findFirst().orElse(null);
    }

    @Override
    public Product modifyProduct(int id, Product newProduct) {
        for (Product product : this.productStorage) {
            if (product.getID() == id) {
                product.setName(newProduct.getName());
                product.setCategory(newProduct.getCategory());
                product.setStock(newProduct.getStock());
                product.setPrice(newProduct.getPrice());
                product.setExpDate(newProduct.getExpDate());
                product.setupdDate(LocalDate.now());
                return product;
            }
        }
        return null;
    }

    @Override
    public Boolean deleteProduct(int id) {
        return this.productStorage.removeIf(product -> product.getID() == id);
    }

    @Override
    public void outOfStock(int id) {
        for (Product product : productStorage) {
            if (product.getID() == id) {
                product.setStock(0);
            }
        }
    }

    @Override
    public void withStock(int id) {
        for (Product product : productStorage) {
            if (product.getID() == id) {
                product.setStock(10);
            }
        }
    }

    @Override
    public List<Product> findByFilters(String name, String category, String availability) {
        return this.productStorage.stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> category == null || p.getCategory().equalsIgnoreCase(availability))
                .filter(p -> {
                    if (availability == null || availability.equalsIgnoreCase("all")) {
                        return true;
                    } else if (availability.equalsIgnoreCase("In stock")) {
                        return p.getStock() > 0;
                    } else if (availability.equalsIgnoreCase("Out of stock")) {
                        return p.getStock() < 0;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

}
