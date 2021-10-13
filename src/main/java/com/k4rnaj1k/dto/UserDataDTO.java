package com.k4rnaj1k.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDataDTO(@JsonProperty("userid") Long userId) {
}
