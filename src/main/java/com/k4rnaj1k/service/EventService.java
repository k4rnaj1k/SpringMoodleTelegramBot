package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.dto.upcoming.UpcomingView;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EventService {
    private WebClient webClient;

    public EventService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void getEvents(String token){
        UpcomingView upcomingView = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_calendar_get_calendar_upcoming_view")
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(UpcomingView.class).block();
        upcomingView.events().forEach(System.out::println);
    }
}
