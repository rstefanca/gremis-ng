package cz.upce.gremis.gremisng.voting.ui;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class PublishRequest {
    private String subject;
    private String body;
    @Nullable
    private LocalDateTime startAt;

    public Optional<LocalDateTime> getStartAt() {
        return Optional.ofNullable(startAt);
    }

    boolean isScheduled() {
        return startAt != null;
    }

}
