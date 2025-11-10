package com.deliverit.global.infrastructure.config.kakao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

@Configuration
public class KakaoMapConfig {

    @Bean
    public RestTemplate kakaoRestTemplate(KakaoProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.getTimeout().getConnect().toMillis());
        factory.setReadTimeout((int) props.getTimeout().getRead().toMillis());

        RestTemplate rt = new RestTemplate(factory);
        rt.setUriTemplateHandler(new DefaultUriBuilderFactory(props.getBaseUrl()));
        rt.getInterceptors().add((req, body, ex) -> {
            req.getHeaders().set("Authorization", "KakaoAK " + props.getRestApiKey());
            return ex.execute(req, body);
        });
        return rt;
    }

    @Getter
    @Setter
    @Component
    @ConfigurationProperties(prefix = "kakao")
    public static class KakaoProperties {
        private String restApiKey;
        private String baseUrl;
        private Timeout timeout = new Timeout();

        @Getter
        @Setter
        public static class Timeout {
            private Duration connect;
            private Duration read;
        }
    }
}