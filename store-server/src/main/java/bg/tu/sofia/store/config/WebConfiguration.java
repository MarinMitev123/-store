package bg.tu.sofia.store.config; // Новият пакет

import bg.tu.sofia.store.security.SecurityConstants; // Промененим импорт
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    // Конфигуриране на CORS (Cross-Origin Resource Sharing)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders(SecurityConstants.TOKEN_HEADER)
                .maxAge(MAX_AGE_SECS);
    }

    // Конфигуриране на ResourceHandlers за обработка на заявки към ресурси
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:tmp/");
    }

    // Конфигуриране на Tomcat конфигурацията за управление на големината на заявките
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> getTomcatCustomizer() {
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
            @Override
            public void customize(TomcatServletWebServerFactory factory) {
                factory.addConnectorCustomizers(
                        (connector) -> {
                            if ((connector.getProtocolHandler()
                                    instanceof AbstractHttp11Protocol<?>)) {
                                // -1 означава неограничено
                                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler())
                                        .setMaxSwallowSize(11534336); // 11 MB
                            }
                            connector.setMaxPostSize(11534336); // 11 MB
                        });
            }
        };
    }
}
