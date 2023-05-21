package cz.upce.gremis.gremisng.time;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.Period;

public interface DateInterval {
    LocalDate getStart();

    LocalDate getEnd();

    default boolean overlaps(DateInterval reference) {
        Assert.notNull(reference, "Reference must not be null!");

        //(StartA <= EndB) and (EndA >= StartB)
        boolean endsAfterOtherStarts = getEnd().isAfter(reference.getStart()) || getEnd().equals(reference.getStart());
        boolean startsBeforeOtherEnds = getStart().isBefore(reference.getEnd()) || getStart().equals(reference.getEnd());

        return startsBeforeOtherEnds && endsAfterOtherStarts;
    }

    @JsonIgnore
    default Period getDuration() {
        return Period.between(getStart(), getEnd());
    }

    default boolean containsDate(LocalDate aDate) {
        return getStart().isBefore(aDate) && getEnd().isAfter(aDate);
    }

    default boolean containsInterval(DateInterval other) {
        return (!getStart().isAfter(other.getStart()))
               && (!getEnd().isBefore(other.getEnd()));
    }

    static DateInterval of(LocalDate start, LocalDate end) {
        Assert.notNull(start, "Start must not be null!");
        Assert.notNull(end, "End must not be null!");
        Assert.isTrue(start.isBefore(end) || start.isEqual(end), "Start must be before or equal to end!");

        return new DateIntervalImpl(start, end);
    }

    static String prettyPrint(DateInterval dateInterval) {
        return String.format("(start=%s, end=%s)", dateInterval.getStart(), dateInterval.getEnd());
    }

    @Value
    class DateIntervalImpl implements DateInterval {
        LocalDate start;
        LocalDate end;
    }

    DateInterval FOREVER = of(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1));
}
