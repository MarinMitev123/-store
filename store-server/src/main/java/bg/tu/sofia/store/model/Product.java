package bg.pgmet.mitev.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@Builder
@EqualsAndHashCode(of = {"name", "id"})
@JsonIgnoreProperties(value = {"comments"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Име на продукта
    @NotNull(message = "Name cannot be null")
    @Length(min = 1, max = 100, message = "Name length must be between 1 and 100")
    private String name;

    // Описание на продукта
    @Length(max = 1000, message = "Description length must be maximum 1000")
    private String description;

    // Категория на продукта (хранителна добавка, аксесоар или облекло)
    @NotNull(message = "Category cannot be null")
    private String category;

    // Цена на продукта
    @Min(value = 0, message = "Price must be at least 0")
    @NotNull(message = "Price cannot be null")
    private Double price;

    // Наличност на продукта
    @Min(value = 0, message = "Stock must be at least 0")
    @NotNull(message = "Stock cannot be null")
    private Integer stock;

    // URL адрес към изображение на продукта
    private String imageUrl;

    // Дата и час на създаване на продукта
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Списък от коментари, свързани с продукта
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "product",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
