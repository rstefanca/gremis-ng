package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.time.DateInterval;
import org.jetbrains.annotations.NotNull;


import java.time.LocalDate;

public record TermDto(Long id, String description, LocalDate start, LocalDate end) implements DateInterval {

    @Override
    public @NotNull LocalDate getStart() {
        return start;
    }

    @Override
    public  @NotNull LocalDate getEnd() {
        return end;
    }
}
