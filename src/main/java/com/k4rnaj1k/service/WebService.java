package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.GroupDTO;
import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.dto.UserGroupsDTO;
import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.dto.upcoming.UpcomingView;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WebService {
    private final WebClient webClient;

    public WebService(WebClient webClient) {
        this.webClient = webClient;
    }

    public UserTokenDTO getToken(String username, String password) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/login/token.php")
                .queryParam("service", "moodle_mobile_app")
                .queryParam("username", username)
                .queryParam("password", password)
                .build()
        ).retrieve().bodyToMono(UserTokenDTO.class).block();
    }

    public List<GroupDTO> getGroups(String token){
        UserGroupsDTO list = webClient.get().uri(uriBuilder -> uriBuilder.path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_group_get_course_user_groups")
                .queryParam("moodlewsrestformat", "json")
                .build()
        ).retrieve().bodyToMono(UserGroupsDTO.class).block();
        return list!=null? list.groups(): null;
    }

    public List<UpcomingEventDTO> getEvents(String token){
        UpcomingView upcomingView = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_calendar_get_calendar_upcoming_view")
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(UpcomingView.class).block();
        return upcomingView != null ? upcomingView.events() : null;
    }
}
