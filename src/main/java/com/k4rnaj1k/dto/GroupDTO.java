package com.k4rnaj1k.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GroupDTO(Long id, String name, @JsonProperty("courseid") Long courseId) {
}
