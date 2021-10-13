package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.*;
import com.k4rnaj1k.dto.upcoming.UpcomingView;
import com.k4rnaj1k.exception.BotExceptionUtils;
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

    public List<GroupDTO> getGroups(String token) {
        UserGroupsDTO list = webClient.get().uri(uriBuilder -> uriBuilder.path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_group_get_course_user_groups")
                .queryParam("moodlewsrestformat", "json")
                .build()
        ).retrieve().bodyToMono(UserGroupsDTO.class).block();
        return list != null ? list.groups() : null;
    }

    public List<UpcomingEventDTO> loadEvents(String token) {
        UpcomingView upcomingView = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_calendar_get_calendar_upcoming_view")
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(UpcomingView.class).block();
        return upcomingView != null ? upcomingView.events() : null;
    }

    public List<CourseDTO> getCourses(String token, Long userId) {
        CourseDTO[] usersCourses = webClient.get().uri(uriBuilder -> uriBuilder
                .path("webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_enrol_get_users_courses")
                .queryParam("userid", userId)
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(CourseDTO[].class).block();
        if (usersCourses == null)
            throw BotExceptionUtils.courseParseFailed(userId);
        return List.of(usersCourses);
    }

    public Long getUserId(String token) {
        UserDataDTO userDataDTO = webClient.get().uri(uriBuilder -> uriBuilder
                .path("webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_webservice_get_site_info")
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(UserDataDTO.class).block();
        if (userDataDTO == null)
            throw BotExceptionUtils.didntRetrieveUserId();
        return userDataDTO.userId();
    }
}
