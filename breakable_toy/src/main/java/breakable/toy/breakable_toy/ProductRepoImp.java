package breakable.toy.breakable_toy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public synchronized Product addProduct(Product product) {
        product.setID(idCounter.getAndIncrement());
        product.setCreationDate(LocalDate.now());
        product.setUpdDate(null);
        this.productStorage.add(product);
        return product;
    }
    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
    }

    @Override
    public List<Product> getAllProductsSorted(String primarySort, String secondarySort, 
                                            String primaryOrder, String secondaryOrder,
                                            int page, int size) {
        validatePagination(page, size);
    
        // Crear comparador compuesto
        Comparator<Product> comparator = buildComparator(primarySort, secondarySort, primaryOrder, secondaryOrder);
    
        // Aplicar ordenamiento
        List<Product> sortedProducts = productStorage.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    
        // Aplicar paginación
        return paginateList(sortedProducts, page, size);
    }
    
    private Comparator<Product> buildComparator(String primarySort, String secondarySort,
                                             String primaryOrder, String secondaryOrder) {
        Comparator<Product> comparator = (a, b) -> 0;
        
        if (primarySort != null && !primarySort.isEmpty()) {
            comparator = comparator.thenComparing(getComparator(primarySort, primaryOrder));
        }
        
        if (secondarySort != null && !secondarySort.isEmpty()) {
            comparator = comparator.thenComparing(getComparator(secondarySort, secondaryOrder));
        }
        
        return comparator;
    }
    
    private List<Product> paginateList(List<Product> list, int page, int size) {
        int start = page * size;
        if (start >= list.size()) {
            return Collections.emptyList();
        }
        
        int end = Math.min(start + size, list.size());
        return list.subList(start, end);
    }
    
@Override
    public long getTotalProducts() {
        return productStorage.size();
    }

    private Comparator<Product> getComparator(String sortBy, String order) {
        // Si sortBy es null o vacío, retorna un comparador neutral
        if (sortBy == null || sortBy.isEmpty()) {
            return (a, b) -> 0;
        }
    
        // Validar que el campo de ordenamiento exista en la clase Product
        try {
            Product.class.getDeclaredField(sortBy);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid sort column: " + sortBy);
        }
    
        Comparator<Product> comparator;
        switch (sortBy) {
            case "name":
                comparator = Comparator.comparing(Product::getName);
                break;
            case "category":
                comparator = Comparator.comparing(Product::getCategory);
                break;
            case "price":
                comparator = Comparator.comparing(Product::getPrice);
                break;
            case "expDate":
                comparator = Comparator.comparing(Product::getExpDate);
                break;
            case "stock":
                comparator = Comparator.comparing(Product::getStock);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort column: " + sortBy);
        }
    
        return "desc".equalsIgnoreCase(order) ? comparator.reversed() : comparator;
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
                product.setUpdDate(LocalDate.now());
                return product;
            }
        }
        return null;
    }

    @Override
    public synchronized Boolean deleteProduct(int id) {
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
                .filter(p -> category == null || category.size() == 0 ||
                (!category.isEmpty() && category.stream().anyMatch(cat -> p.getCategory().equalsIgnoreCase(cat))))                
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
