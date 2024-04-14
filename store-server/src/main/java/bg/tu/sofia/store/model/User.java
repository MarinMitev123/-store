package bg.tu.sofia.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
// Използване на Lombok за автоматично генериране на getter и setter методи, toString и други
@JsonIgnoreProperties(
        value = {
                "accountNonExpired",
                "accountNonLocked",
                "credentialsNonExpired",
                "enabled",
                "comments"
        })
// Игнориране на определени свойства при сериализация
@JsonPropertyOrder({"id"})
// Подредба на свойствата при сериализация
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
// Шаблонен метод за построяване на обектите
@Transactional
// Анотация за транзакционност
public class User {

    private static final String DEFAULT_IMAGE = "";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Уникален идентификатор на потребителя, автоматично генериран
    private Long id;

    @Length(min = 4, max = 120, message = "Username length must be between 4 and 120")
    @Column(unique = true)
    // Потребителско име, уникално в базата данни
    @NotNull(message = "Username cannot be null")
    private String username;

    @Length(min = 8, max = 100, message = "Password length must be between 8 and 100")
    // Парола за достъп на потребителя
    @NotNull(message = "Password cannot be null")
    private String password;

    @Length(min = 2, max = 30, message = "First Name length must be between 2 and 30")
    // Име на потребителя
    @NotNull(message = "First name cannot be null")
    private String firstName;

    @Length(min = 2, max = 30, message = "Last Name length must be between 2 and 30")
    // Фамилно име на потребителя
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @Builder.Default private String imageUrl = DEFAULT_IMAGE;
    // URL адрес на профилната снимка на потребителя

    private bg.tu.sofia.store.model.enums.Role role;
    // Ролята на потребителя в системата (например, администратор, потребител и т.н.)

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @ToString.Exclude
    // Изключване на коментарите от метода toString(), за да се избегне рекурсивност
    private List<Comment> comments = new ArrayList<>();
    // Списък с коментарите написани от потребителя

    @Builder.Default private boolean active = true;
    // Флаг за активност на потребителя

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    // Дата и час на регистрация на потребителя, със стандартен формат
    private LocalDateTime registered = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    // Дата и час на последна актуализация на потребителя, със стандартен формат
    private LocalDateTime updated = LocalDateTime.now();
}
