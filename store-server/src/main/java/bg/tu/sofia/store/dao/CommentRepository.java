package bg.tu.sofia.store.dao;

import bg.tu.sofia.store.model.Comment;
import bg.tu.sofia.store.model.Product;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional(readOnly = true)
    Set<Comment> findByProduct(Product product);
}
