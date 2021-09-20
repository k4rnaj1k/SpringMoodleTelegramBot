package com.k4rnaj1k.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserTokenDTO(String token,
                           @JsonProperty("privatetoken")
                           String privateToken) {
}
