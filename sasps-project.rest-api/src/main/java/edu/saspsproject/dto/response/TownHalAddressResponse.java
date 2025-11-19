package edu.saspsproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TownHalAddressResponse {
    @JsonProperty("strada")
    private String street;

    @JsonProperty("localitate")
    private TownHallCityResponse city;

    @JsonProperty("cod-postal")
    private String postalCode;
}
