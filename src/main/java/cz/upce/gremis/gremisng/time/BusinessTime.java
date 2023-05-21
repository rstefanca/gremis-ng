package cz.upce.gremis.gremisng.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BusinessTime {

    LocalDateTime getDateTime();

    LocalDate getDate();
}
