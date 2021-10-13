package com.k4rnaj1k.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

import static com.k4rnaj1k.model.Event.ModuleName;

public record UpcomingEventDTO(
        Long id,
        String name,
        CourseDTO course,
        @JsonProperty("timestart")
        Instant timeStart,
        String url,
        @JsonProperty("modulename")
        ModuleName moduleName,
        Long groupid) {
}
