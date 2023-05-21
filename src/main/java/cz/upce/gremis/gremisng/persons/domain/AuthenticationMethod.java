package cz.upce.gremis.gremisng.persons.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthenticationMethod {
    @JsonProperty("N")
    NONE,
    @JsonProperty("E")
    EXTERNAL,
    @JsonProperty("L")
    LOCAL
}
