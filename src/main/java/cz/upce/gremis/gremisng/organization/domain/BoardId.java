package cz.upce.gremis.gremisng.organization.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BoardId(@NotNull Long value) {

    private static final BoardId NULL_ID = new BoardId(-1L);

    public static BoardId of(@Nullable Long value) {
        if (value == null) {
            return NULL_ID;
        }

        return new BoardId(value);
    }
}
