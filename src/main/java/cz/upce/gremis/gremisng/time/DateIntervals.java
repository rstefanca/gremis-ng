package cz.upce.gremis.gremisng.time;

public final class DateIntervals {

    public static DateInterval MAX = DateInterval.of(Dates.MIN, Dates.MAX);

    public static boolean areEqual(DateInterval first, DateInterval second) {
        return first.getStart().equals(second.getStart())
               && first.getEnd().equals(second.getEnd());
    }

}
