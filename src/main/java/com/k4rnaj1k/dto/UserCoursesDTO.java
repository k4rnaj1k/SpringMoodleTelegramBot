package com.k4rnaj1k.dto;

import com.k4rnaj1k.dto.upcoming.CourseDTO;

import java.util.List;

public record UserCoursesDTO(List<CourseDTO> courses) {
}
