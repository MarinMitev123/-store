package bg.tu.sofia.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
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
@EqualsAndHashCode(of = {"model", "id"})
@JsonIgnoreProperties(value = {"comments"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 60, message = "Model name length must be between 1 and 60")
    @Column(unique = true)
    @NotNull(message = "Model cannot be null")
    private String model;

    @NotNull(message = "Brand type cannot be null")
    private String brandType;

    @NotNull(message = "Food type cannot be null")
    private String foodType;

    @NotNull(message = "Category cannot be null")
    private String category;

    @Builder.Default private Boolean onSale = false;

    @NotNull(message = "Product category cannot be null")
    private String productCategory;

    @Min(value = 1, message = "Price must be at least 1 $")
    @NotNull(message = "Price cannot be null")
    private Double price;

    @Builder.Default
    @Min(value = 0, message = "Discount % must be at least 0")
    @Max(value = 100, message = "Discount % must be maximum 100")
    private Integer percentOff = 0;
    // URL адрес към изображение на продукта
    private String imageUrl;

    // Дата и час на създаване на продукта
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime released = LocalDateTime.now();

    // Списък от коментари, свързани с продукта
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "product",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();
}
