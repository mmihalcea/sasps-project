package edu.saspsproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TownHallCityResponse {
    @JsonProperty("nume")
    private String name;
}
