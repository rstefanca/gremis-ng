package cz.upce.gremis.gremisng.voting.domain;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Vote}
 */
public record VoteDto(
        Long id,
        Long boardTermId,

        @NotNull String name,
        VoteState voteState,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long boardId,
        String boardName) implements Serializable {
}