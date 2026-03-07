package com.intensivo.java.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cep")
public record CepProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout) {
}
