package com.k4rnaj1k.dto.upcoming;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CourseDTO(Long id,
                        @JsonProperty("fullname")
                        String fullName,
                        @JsonProperty("shortname")
                        String shortName) {

}
