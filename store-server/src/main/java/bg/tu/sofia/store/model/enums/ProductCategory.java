import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
// Анотация за форматиране на JSON обекти
public enum ProductCategory {
    VITAMINS("Vitamins"), // Категория за витамини
    PROTEINS("Proteins"), // Категория за протеини
    SUPPLEMENTS("Supplements"), // Категория за добавки
    HERBAL_REMEDIES("Herbal Remedies"), // Категория за билкови лекарства
    HEALTH_FOOD("Health Food"), // Категория за здравословни храни
    FITNESS_EQUIPMENT("Fitness Equipment"), // Категория за фитнес оборудване
    CLOTHING("Clothing"), // Категория за облекло
    ACCESSORIES("Accessories"), // Категория за аксесоари
    ;

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    @JsonValue
    // Анотация, указваща кой метод да се използва за сериализация на стойността на енумерацията
    public String getLabel() {
        return label;
    }

    @JsonCreator
    // Анотация, която указва метода, който да се използва за десериализация на JSON обектите в енумерацията
    public static ProductCategory of(String label) {
        return Stream.of(ProductCategory.values())
                .filter(p -> p.getLabel().equals(label))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
