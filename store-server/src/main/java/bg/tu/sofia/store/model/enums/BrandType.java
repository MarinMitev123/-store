package bg.tu.sofia.store.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.stream.Stream;

public enum BrandType {
    VITABUDDY("VitaBuddy"),     // Марка VitaBuddy
    NUTRIBEST("NutriBest"),     // Марка NutriBest
    PURENATURE("PureNature"),   // Марка PureNature
    HERBALSENSE("HerbalSense"), // Марка HerbalSense
    ORGANICA("Organica"),       // Марка Organica
    FITGURU("FitGuru"),         // Марка FitGuru
    ;

    private final String label; // Поле за съхранение на марката

    BrandType(String label) { // Конструктор с параметър за марката
        this.label = label;
    }

    @Override
    public String toString() { // Пренаписване на метода toString()
        return label;
    }

    @JsonValue
    public String getLabel() { // Метод за връщане на марката
        return label;
    }

    @JsonCreator
    public static BrandType of(String label) { // Статичен метод за създаване на енумерация по марка
        return Stream.of(BrandType.values())
                .filter(p -> Objects.equals(p.getLabel(), label))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
