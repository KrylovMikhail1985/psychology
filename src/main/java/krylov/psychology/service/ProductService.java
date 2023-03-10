package krylov.psychology.service;

import krylov.psychology.model.Product;

import java.time.LocalTime;
import java.util.List;

public interface ProductService {
    Product createProduct(String productName,
                          int productCost,
                          LocalTime productDuration,
                          String productDescription,
                          boolean actual,
                          int priority);
    List<Product> findAllProducts();
    Product updateProduct(long id, Product product);
    void deleteProduct(long id);
    Product findById(long id);
    void delete(long id);
}
