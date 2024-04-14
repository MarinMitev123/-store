package bg.tu.sofia.store.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

public enum Category {
    SUPPLEMENTS("Supplements"),
    ACCESSORIES("Accessories"),
    CLOTHING("Clothing"),
    ;

    private final String label;

    Category(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Category of(String label) {
        return Stream.of(Category.values())
                .filter(p -> p.getLabel().equals(label))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
