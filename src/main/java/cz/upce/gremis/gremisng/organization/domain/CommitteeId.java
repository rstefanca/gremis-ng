package cz.upce.gremis.gremisng.organization.domain;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CommitteeId(@NotNull Long value) {

    private static CommitteeId NULL_ID = new CommitteeId(-1L);

    public static CommitteeId of(@Nullable Long value) {
        if (value == null) return NULL_ID;

        return new CommitteeId(value);
    }
}
