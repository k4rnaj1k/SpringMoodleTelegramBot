package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UserDataDTO;
import com.k4rnaj1k.dto.UserGroupsDTO;
import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.dto.upcoming.CourseDTO;
import com.k4rnaj1k.dto.upcoming.GroupDTO;
import com.k4rnaj1k.dto.upcoming.UpcomingEventDTO;
import com.k4rnaj1k.dto.upcoming.UpcomingView;
import com.k4rnaj1k.exception.BotExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class WebService {
    private final WebClient webClient;

    public WebService(WebClient webClient) {
        this.webClient = webClient;
    }

    public UserTokenDTO getToken(String username, String password) {
        log.info("getToken - Loading user's token.");
        UserTokenDTO userTokenDTO = webClient.get().uri(uriBuilder -> uriBuilder.path("/login/token.php")
                .queryParam("service", "moodle_mobile_app")
                .queryParam("username", username)
                .queryParam("password", password)
                .build()
        ).retrieve().bodyToMono(UserTokenDTO.class).block();
        if (userTokenDTO == null || userTokenDTO.token() == null) {
            log.error("getToken - couldn't retrieve users token.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't log in. Wrong credentials.");
        } else {
            return userTokenDTO;
        }
    }

    public List<GroupDTO> getGroups(String token) {
        log.info("getGroups - loading user's groups.");
        UserGroupsDTO list = webClient.get().uri(uriBuilder -> uriBuilder.path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_group_get_course_user_groups")
                .queryParam("moodlewsrestformat", "json")
                .build()
        ).retrieve().bodyToMono(UserGroupsDTO.class).block();
        return list != null ? list.groups() : null;
    }

    public List<UpcomingEventDTO> loadEvents(String token) {
        log.info("loadEvents - loading user's events.");
        UpcomingView upcomingView = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/webservice/rest/server.php")
                .queryParam("wstoken", token)
                .queryParam("wsfunction", "core_calendar_get_calendar_upcoming_view")
                .queryParam("moodlewsrestformat", "json")
                .build()).retrieve().bodyToMono(UpcomingView.class).block();
        return upcomingView != null ? upcomingView.events() : null;
    }

    public List<CourseDTO> getCourses(String token, Long userId) {
        log.info("getCourses - loading user's courses.");
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
        log.info("getUserId - loading user's id.");
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
