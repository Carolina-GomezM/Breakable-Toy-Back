package breakable.toy.breakable_toy;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo {

    public Product addProduct(Product product);

    List<Product> getAllProducts();

    public Product modifyProduct(int id, Product product);

    public Product searchId(int id);

    public Boolean deleteProduct(int id);

    public void outOfStock(int id);

    public void withStock(int id);

    List<Product> findByFilters(String name, List<String> category, String availability);

    List<String> obtainCategories();

    List<Reports> obtainReports(); 

    

}
