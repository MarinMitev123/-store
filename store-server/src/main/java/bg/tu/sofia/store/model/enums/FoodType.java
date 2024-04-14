package bg.tu.sofia.store.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

// Енумерация, представляваща типовете ястия
public enum FoodType {
    BEEF_STEAK("Beef Steak"),
    CHICKEN_BREAST("Chicken Breast"),
    SALMON_FILLET("Salmon Fillet"),
    SPINACH("Spinach"),
    TOMATO("Tomato"),
    APPLE("Apple"),
    BANANA("Banana"),
    ;

    private final String label;  // Текстово представяне на видя ястие

    // Конструктор за инициализиране на видя ястие със зададено текстово представяне
    FoodType(String label) {
        this.label = label;
    }

    // Метод за връщане на текстовото представяне на видя ястие
    @Override
    public String toString() {
        return label;
    }

    // Метод за връщане на текстовото представяне на видя ястие при сериализация
    @JsonValue
    public String getLabel() {
        return label;
    }
}
