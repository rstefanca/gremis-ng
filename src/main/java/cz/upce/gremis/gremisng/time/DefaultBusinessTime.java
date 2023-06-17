package cz.upce.gremis.gremisng.time;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DefaultBusinessTime implements BusinessTime {

    @Override
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.now();
    }
}
