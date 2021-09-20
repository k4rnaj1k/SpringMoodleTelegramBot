package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UserTokenDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TokenService {
    private WebClient webClient;

    public TokenService(WebClient webClient) {
        this.webClient = webClient;
    }

    public UserTokenDTO getToken(String username, String password){
       UserTokenDTO res = webClient.get().uri(uriBuilder -> uriBuilder.path("/login/token.php")
               .queryParam("service", "moodle_mobile_app")
               .queryParam("username", username)
               .queryParam("password", password)
               .build()
       ).attribute("service", "").retrieve().bodyToMono(UserTokenDTO.class).block();
        return res;
    }
}
