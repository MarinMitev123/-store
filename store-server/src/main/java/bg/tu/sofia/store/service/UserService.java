package bg.tu.sofia.store.service;

import bg.tu.sofia.store.model.User;
import java.util.Set;

public interface UserService {
    // Връща множество от всички потребители в системата.
    Set<User> getAllUsers();

    // Връща потребител по зададен уникален идентификатор.
    User getUserById(Long id);

    // Връща потребител по зададено потребителско име.
    User getUserByUsername(String username);

    // Създава нов потребител в системата.
    User createUser(User user);

    // Създава нов потребител в системата, със или без криптиране на паролата в зависимост от параметъра safe.
    User createUser(User user, boolean safe);

    // Актуализира информацията за съществуващ потребител в системата.
    User updateUser(User user);

    // Изтрива потребител по зададен уникален идентификатор.
    User deleteUser(Long id);
}

