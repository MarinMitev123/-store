package bg.tu.sofia.store.service;

import bg.tu.sofia.store.model.Product;

import java.util.Set;

public interface ProductService {
    Set<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    Product deleteProduct(Long id);

    Set<Product> getProductsByModelContains(String title);
    Set<Product> getProductsByBrandType(String brandType);

    Set<Product> getProductsByCategory(String category);

    Set<Product> getProductsByFoodType(String foodType);

    Set<Product> getProductsByProductCategory(String productCategory);

    Set<Product> getProductsByOnSale(boolean onSale);

    Set<Product> getNewProducts();


    //Set<Product> getProductsByRole(String role);
}

