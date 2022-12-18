package krylov.psychology.service;

import krylov.psychology.model.Product;
import krylov.psychology.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product findById(long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @Override
    public Product createProduct(String productName,
                                 int productCost,
                                 String productDuration,
                                 String productDescription,
                                 boolean actual,
                                 int priority) {
        Product product = new Product(productName, productCost, productDuration, productDescription, actual, priority);
        return productRepository.save(product);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAllByOrderByPriority().orElseThrow();
    }

    @Override
    public Product updateProduct(long id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void delete(long id) {
        productRepository.deleteById(id);
    }
}
