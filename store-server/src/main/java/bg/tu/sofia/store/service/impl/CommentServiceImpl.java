package bg.tu.sofia.store.service.impl;

import bg.tu.sofia.store.dao.CommentRepository;
import bg.tu.sofia.store.exception.EntityNotFoundException;
import bg.tu.sofia.store.model.Comment;
import bg.tu.sofia.store.model.Product;
import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repo;

    // Взема коментар по зададен уникален идентификатор
    @Override
    public Comment getCommentById(Long id) {
        if (id == null) {
            return null;
        }
        Comment comment =
                repo.findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                String.format(
                                                        "Comment with ID=%s does not exist.", id)));
        return modifyCommentSafeUser(comment);
    }

    // Взема всички коментари за даден продукт
    @Override
    public Set<Comment> getCommentsByProduct(Product product) {
        if (product == null) {
            return null;
        }
        return repo.findByProduct(product).stream()
                .map(this::modifyCommentSafeUser)
                .collect(Collectors.toSet());
    }

    // Актуализира коментар
    @Override
    public Comment updateComment(Comment comment) {
        comment.setEdited(LocalDateTime.now());
        Comment saved = repo.save(comment);
        modifyCommentSafeUser(saved);
        return saved;
    }

    // Създава нов коментар
    @Override
    public Comment createComment(Comment comment) {
        comment.setCreated(LocalDateTime.now());
        comment.setEdited(LocalDateTime.now());
        return insert(comment);
    }

    // Помощен метод за вмъкване на коментар в базата данни
    @Transactional
    private Comment insert(Comment comment) {
        comment.setId(null);
        Comment saved = repo.save(comment);
        modifyCommentSafeUser(saved);
        return saved;
    }

    // Изтрива коментар по зададен уникален идентификатор
    @Override
    public Comment deleteComment(Long id) {
        Comment old = getCommentById(id);
        repo.deleteById(id);
        modifyCommentSafeUser(old);
        return old;
    }

    // Модифицира коментара за да се върне само част от данните за потребителя
    private Comment modifyCommentSafeUser(Comment comment) {
        User user = comment.getUser();
        User returnUser =
                User.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .registered(null)
                        .updated(null)
                        .build();
        comment.setUser(returnUser);
        return comment;
    }
}

