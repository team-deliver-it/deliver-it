package com.deliverit.global.infrastructure.config.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class GeminiConfig {

    @Value("${ai.authorization_header}")
    private String AUTHORIZATION;

    @Value("${ai.token}")
    private String token;

    @Value("${ai.connect_time}")
    private int connectTime;

    @Value("${ai.read_time}")
    private int readTime;

    @Bean
    public SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTime));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTime));
        return requestFactory;
    }

    @Bean
    @Qualifier("geminiClient")
    public RestTemplate geminiRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.requestFactory(this::requestFactory)
                .defaultHeader(AUTHORIZATION, token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .errorHandler(new GeminiRestTemplateErrorHandler())
                .build();
    }
}
