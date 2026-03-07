package com.intensivo.java.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({CepProperties.class, DefaultUsersProperties.class})
public class RestClientConfig {

    @Bean
    RestClient viaCepRestClient(CepProperties cepProperties) {
        return RestClient.builder()
                .baseUrl(cepProperties.baseUrl())
                .requestFactory(requestFactory(cepProperties.connectTimeout(), cepProperties.readTimeout()))
                .build();
    }

    private SimpleClientHttpRequestFactory requestFactory(Duration connectTimeout, Duration readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Math.toIntExact(connectTimeout.toMillis()));
        factory.setReadTimeout(Math.toIntExact(readTimeout.toMillis()));
        return factory;
    }
}
