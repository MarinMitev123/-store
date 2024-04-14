package bg.tu.sofia.store.security;

import bg.tu.sofia.store.utils.ValidationUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;

// Анотация за неизменяем клас с финални полета и методи за достъп до тях
@Value
// Анотация за генериране на вложен клас за построяване на обекти чрез паттерна Builder
@Builder
// Анотация, която указва Jackson как да десериализира JSON в обекта Credentials
@JsonDeserialize(builder = Credentials.ValidatedCredentialsBuilder.class)
public class Credentials {
    // Анотация, която указва, че полето не може да бъде null
    @NotNull
    private String username;
    // Анотация, която указва, че полето не може да бъде null
    @NotNull
    private String password;

    // Анотация, която указва Jackson как да създаде Builder класа за десериализация на JSON обекти
    @JsonPOJOBuilder
    public static class ValidatedCredentialsBuilder extends CredentialsBuilder {
        // Пренаписване на метода за създаване на обект, за да въведем валидация
        @Override
        public Credentials build() {
            // Извикване на метода за валидация от ValidationUtils и връщане на валидиран обект
            return ValidationUtils.validate(super.build());
        }
    }
}
