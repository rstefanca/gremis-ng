package cz.upce.gremis.gremisng.time;

import lombok.experimental.UtilityClass;

import java.time.Period;

@UtilityClass
public class PeriodUtils {

    public Period fromTotalMonths(int totalMonths) {
        int years = totalMonths / 12;
        int months = totalMonths % 12;

        return Period.of(years, months, 0);
    }
}
