package cz.upce.gremis.gremisng.voting.domain;

import java.time.LocalDateTime;

public record BallotDto(long id, String ipAddress, LocalDateTime timestamp) {

    public String voterFullName() {
        return "Tod Ovic";
    }
}
