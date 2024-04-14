package bg.tu.sofia.store.service.impl;

import bg.tu.sofia.store.dao.UserRepository;
import bg.tu.sofia.store.exception.EntityNotFoundException;
import bg.tu.sofia.store.exception.StoreException;
import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    private UserRepository repo;

    @Autowired
    public void setUserRepository(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    // Имплементация на метод за взимане на всички потребители в системата
    public Set<User> getAllUsers() {
        // Връща копие на Set с всички потребители в системата
        return new HashSet<>(repo.findAll());
    }

    @Override
    // Имплементация на метод за взимане на потребител по ID
    public User getUserById(Long id) {
        // Проверка за невалидно ID
        if (id == null) return null;
        // Връща потребител по ID или хвърля изключение, ако не е намерен
        return repo.findById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        String.format("User with id=%s does not exist", id)));
    }

    @Override
    // Имплементация на метод за взимане на потребител по потребителско име
    public User getUserByUsername(String username) {
        // Връща потребител по потребителско име, ако съществува, в противен случай - null
        Optional<User> result = repo.findByUsername(username);
        return result.isPresent() ? result.get() : null;
    }

    @Override
    // Имплементация на метод за създаване на потребител
    public User createUser(User user) {
        // Извиква метода за създаване на потребител с параметър safe=false
        return this.createUser(user, false);
    }

    @Override
    // Имплементация на метод за създаване на потребител с опция за безопасност
    public User createUser(User user, boolean safe) {
        // Проверява дали потребител със същото потребителско име вече съществува
        Optional<User> result = repo.findByUsername(user.getUsername());
        // Ако потребителят вече съществува и safe=false, хвърля изключение
        if (result.isPresent() && !safe) {
            throw new StoreException(
                    String.format("User with username=%s already exists.", user.getUsername()));
        }
        // Връща съществуващия потребител, ако той вече съществува и safe=true
        else if (result.isPresent()) {
            return result.get();
        } else {
            // Иначе създава нов потребител с текущата дата и време на регистрация и актуализация
            user.setRegistered(LocalDateTime.now());
            user.setUpdated((LocalDateTime.now()));
            user.setActive(true);
            log.info("Creating default user: {}", user);
            // Викане на метода за вмъкване на новия потребител в репозитория
            return insert(user);
        }
    }

    @Transactional
    // Приватен метод за вмъкване на потребител в репозитория
    private User insert(User user) {
        user.setId(null); // Задаване на ID на null, за да се генерира нов уникален идентификатор
        return repo.save(user);
    }

    @Override
    // Имплементация на метод за актуализиране на потребител
    public User updateUser(User user) {
        user.setUpdated(LocalDateTime.now()); // Задаване на актуализирана дата и време
        return repo.save(user); // Запазване на актуализирания потребител в репозитория
    }

    @Override
    // Имплементация на метод за изтриване на потребител
    public User deleteUser(Long id) {
        // Вземане на потребител по зададен ID
        User old = getUserById(id);
        repo.deleteById(id); // Изтриване на потребителя от репозитория
        return old; // Връщане на изтрития потребител
    }
}

