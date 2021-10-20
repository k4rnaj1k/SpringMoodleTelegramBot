package com.k4rnaj1k.configuration;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Value("${moodle.url}")
    private String moodleUrl;

    @Bean
    public WebClient webClient() {
        SslContextFactory sslContextFactory = new SslContextFactory.Client(true);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        HttpClient httpClient = new HttpClient(sslContextFactory);
        ClientHttpConnector connector = new JettyClientHttpConnector(httpClient);

        return WebClient.builder().baseUrl(moodleUrl).clientConnector(connector).build();
    }

}
