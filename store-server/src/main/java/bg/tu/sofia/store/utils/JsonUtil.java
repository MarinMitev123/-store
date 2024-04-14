package bg.tu.sofia.store.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static JsonNode toJsonNode(String jsonString) {
        try {
            return MAPPER.readTree(jsonString);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @SneakyThrows
    public static <T> T mapObject(String body, Class<T> tClass) {
        return MAPPER.readValue(body, tClass);
    }

    @SneakyThrows
    public static <T> String toStringObject(T object) {
        return MAPPER.writeValueAsString(object);
    }
}

