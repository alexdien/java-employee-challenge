package com.reliaquest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean for HTTP client operations.
     *
     * @return a {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
