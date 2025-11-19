package edu.saspsproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TownHallContactResponse {

    @JsonProperty("telefon")
    List<String> phoneNumbers;
}
