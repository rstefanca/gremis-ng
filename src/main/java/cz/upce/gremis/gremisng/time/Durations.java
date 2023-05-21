package cz.upce.gremis.gremisng.time;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.time.Period;

@UtilityClass
public class Durations {

    public static @Nullable Period safeMonths(@Nullable Integer src) {
        if (src == null) {
            return null;
        }

        return Period.ofMonths(src);
    }

}
