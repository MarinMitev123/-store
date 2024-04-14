package bg.tu.sofia.store.service;

import bg.tu.sofia.store.model.Comment;
import bg.tu.sofia.store.model.Product;
import java.util.Set;

// Интерфейс за управление на коментарите в системата
public interface CommentService {
    Set<Comment> getCommentsByProduct(Product product);

    Comment getCommentById(Long id);

    Comment updateComment(Comment comment);

    Comment createComment(Comment comment);

    Comment deleteComment(Long id);
}

