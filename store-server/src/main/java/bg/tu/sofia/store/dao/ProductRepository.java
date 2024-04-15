package bg.tu.sofia.store.dao;

import bg.tu.sofia.store.model.Product;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Transactional(readOnly = true)
    Set<Product> findByProductCategory(String productType);

    @Transactional(readOnly = true)
    Set<Product> findByBrandType(String brand);

    @Transactional(readOnly = true)
    Set<Product> findByCategory(String category);

    @Transactional(readOnly = true)
    Set<Product> findByFoodType(String foodType);

    @Transactional(readOnly = true)
    Set<Product> findByOnSale(boolean onSale);

    Product findByModel(String model);
}
