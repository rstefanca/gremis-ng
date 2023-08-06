package cz.upce.gremis.gremisng.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface BusinessTime {

    LocalDateTime getDateTime();

    LocalDate getDate();

    default LocalDateTime nextMinutes(int step) {
        var now = getDateTime();
        var delta = now.getMinute() % 15;
        return now.plusMinutes(15 - delta).truncatedTo(ChronoUnit.MINUTES);
    }
}
