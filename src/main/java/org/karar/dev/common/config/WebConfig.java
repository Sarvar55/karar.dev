package org.karar.dev.common.config;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ApiVersionDeprecationHandler;
import org.springframework.web.accept.StandardApiVersionDeprecationHandler;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        private final String DEPRECATION_VERSION = "1.0";
        private final String PREFIX = "/api";
        private final String SUNSET_LINK = "https://karardev.com/v1-sunset/";
        private final String[] SUPPORTED_VERSIONS = { "1.0", "2.0", "3.0" };

        @Override
        public void configureApiVersioning(ApiVersionConfigurer configurer) {
                configurer
                                .useMediaTypeParameter(MediaType.parseMediaType("application/vnd.karar.dev+json"), "v")
                                .addSupportedVersions(SUPPORTED_VERSIONS)
                                .setDefaultVersion(DEPRECATION_VERSION);
        }

        @Bean
        public ApiVersionDeprecationHandler apiVersionDeprecationHandler() {
                StandardApiVersionDeprecationHandler deprecationHandler = new StandardApiVersionDeprecationHandler();

                ZonedDateTime deprecationDate = ZonedDateTime.of(
                                LocalDateTime.of(2026, 6, 1, 0, 0), ZoneId.of("UTC"));

                ZonedDateTime sunsetDate = ZonedDateTime.of(
                                LocalDateTime.of(2026, 12, 31, 23, 59), ZoneId.of("UTC"));

                deprecationHandler.configureVersion(DEPRECATION_VERSION)
                                .setDeprecationDate(deprecationDate)
                                .setSunsetDate(sunsetDate)
                                .setSunsetLink(URI.create(SUNSET_LINK));

                return deprecationHandler;
        }

        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
                /*
                 * Because of controllerType -> true, Spring Boot was attaching the
                 * /api prefix to all controllers, including the internal ones provided by
                 * springdoc-openapi. This meant Swagger UI was trying to load /v3/api-docs,
                 * but your server had moved it to /api/v3/api-docs!
                 */
                configurer.addPathPrefix(PREFIX,
                                controllerType -> controllerType.getPackageName().startsWith("org.karar.dev"));
        }

}
