package breakable.toy.breakable_toy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
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
    public List<Product> findByFilters(String name, List<String> category, String availability) {
        return this.productStorage.stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> category == null || !category.isEmpty() && category.stream().anyMatch(cat -> p.getCategory().toLowerCase().contains(cat.toLowerCase())))
                .filter(p -> {
                    if (availability == null || availability.equalsIgnoreCase("All")) {
                        return true;
                    } else if (availability.equalsIgnoreCase("in_stock")) {
                        return p.getStock() > 0;
                    } else if (availability.equalsIgnoreCase("out_of_stock")) {
                        return p.getStock() <= 0;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> obtainCategories(){
        return this.productStorage.stream().map(Product::getCategory).distinct().sorted().collect(Collectors.toList());

    }

    @Override
    public List<Reports> obtainReports(){
        Map<String, List<Product>> productsCategory = this.productStorage.stream().collect(Collectors.groupingBy(Product::getCategory));

        List<Reports> categoryReports = new ArrayList<>();

        double overallTotalValueInStock = 0;
        long overallTotalProductsInStock = 0;
        double overallTotalPriceInStock = 0;

        for(Map.Entry<String, List<Product>> entry: productsCategory.entrySet()){
            String category = entry.getKey();
            List<Product> products = entry.getValue();

            long totalProductsInStock = products.stream().mapToLong(Product::getStock).sum();
            double totalValueInStock = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();
            double averagePriceInStock = totalValueInStock > 0 ? totalValueInStock/totalProductsInStock : 0;

            Reports categoryReport = Reports.builder()
                .category(category)
                .totalProductsInStock(totalProductsInStock)
                .totalValueInStock(totalValueInStock)
                .averagePriceInStock(averagePriceInStock)
                .build();

            categoryReports.add(categoryReport);

            overallTotalProductsInStock += totalProductsInStock;
            overallTotalValueInStock += totalValueInStock;                

        }

        overallTotalPriceInStock = (overallTotalProductsInStock > 0) ? overallTotalValueInStock / overallTotalProductsInStock : 0;

        Reports categoryReport = Reports.builder()
        .category("Overall")
        .totalProductsInStock(overallTotalProductsInStock)
        .totalValueInStock(overallTotalValueInStock)
        .averagePriceInStock(overallTotalPriceInStock)
        .build();

        categoryReports.add(categoryReport);

        return categoryReports;

    }

}
