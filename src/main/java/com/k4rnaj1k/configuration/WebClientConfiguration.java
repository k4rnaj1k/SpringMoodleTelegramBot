package com.k4rnaj1k.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${moodle.url}")
    private String moodleUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(moodleUrl);
    }

}
