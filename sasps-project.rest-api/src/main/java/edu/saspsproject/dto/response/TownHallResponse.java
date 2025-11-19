package edu.saspsproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TownHallResponse {

    @JsonProperty("nume")
    private String name;

    @JsonProperty("adresa")
    private TownHalAddressResponse address;

    @JsonProperty("contact")
    private TownHallContactResponse contact;
}
