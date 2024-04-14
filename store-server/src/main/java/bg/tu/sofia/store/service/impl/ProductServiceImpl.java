package bg.tu.sofia.store.service.impl;

import bg.tu.sofia.store.dao.ProductRepository;
import bg.tu.sofia.store.exception.EntityAlreadyExistsException;
import bg.tu.sofia.store.exception.EntityNotFoundException;
import bg.tu.sofia.store.model.Product;
import bg.tu.sofia.store.service.CommentService;
import bg.tu.sofia.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CommentService commentService;

    @Override
    public Set<Product> getAllProducts() {
        return new HashSet<>(repo.findAll());
    }

    @Override
    public Product getProductById(Long id) {
        if (id == null) {
            return null;
        }
        return repo.findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        String.format("Product with ID=%s does not exist.", id)));
    }


    @Override
    public Product createProduct(Product product) {
        Product result = repo.findByModel(product.getModel());
        if (result != null) {
            throw new EntityAlreadyExistsException(
                    String.format("Product with model=%s already exists!", product.getProductCategory()));
        } else {
            return insert(product);
        }
    }

    @Transactional
    public Product insert(Product product) {
        product.setId(null);
        return repo.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        if (!product.getOnSale()) {
            product.setPercentOff(0);
        }
        return repo.save(product);
    }

    @Override
    public Set<Product> getProductsByModelContains(String title) {
        return repo.findAll().stream()
                .filter(product -> product.getProductCategory().toUpperCase().contains(title.toUpperCase()))
                .collect(Collectors.toSet());
    }
    @Override
    public Product deleteProduct(Long id) {
        Product old = getProductById(id);
        repo.deleteById(id);
        return old;
    }

    @Override
    public Set<Product> getProductsByBrandType(String brandType) {
        return repo.findByBrandType(brandType);
    }

    @Override
    public Set<Product> getProductsByCategory(String category) {
        return repo.findByCategory(category);
    }

    @Override
    public Set<Product> getProductsByFoodType(String foodType) {

        return repo.findByCategory(foodType);
    }

    @Override
    public Set<Product> getProductsByProductCategory(String productCategory) {
        return repo.findByProductCategory(productCategory);
    }

    @Override
    public Set<Product> getProductsByOnSale(boolean onSale) {
        return repo.findByOnSale(onSale);
    }

    @Override
    public Set<Product> getNewProducts() {
        List<Product> products = repo.findAll();
        products.sort((p2, p1) -> p1.getReleased().compareTo(p2.getReleased()));
        return new HashSet<>(products);
    }
}

