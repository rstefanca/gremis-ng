package cz.upce.gremis.gremisng.persons.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PersonState {
    @JsonProperty("I")
    INITIALIZED,
    @JsonProperty("B")
    BLOCKED,
    @JsonProperty("A")
    ACTIVE,
    @JsonProperty("W")
    WAITING
}
