package cz.upce.gremis.gremisng.organization.domain;

import java.time.LocalDate;

public record TermDto(Long id, String description, LocalDate start, LocalDate end) {
}
