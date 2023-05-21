package cz.upce.gremis.gremisng.assignments.service;

import java.time.LocalDate;

public record NewAssignment(Long boardTermId, Long seatId, Long person, LocalDate start, LocalDate end) {
}
