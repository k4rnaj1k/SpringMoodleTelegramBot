package com.k4rnaj1k.dto.upcoming;

import com.k4rnaj1k.dto.UpcomingEventDTO;

import java.util.List;

public record UpcomingView(List<UpcomingEventDTO> events) {
}
