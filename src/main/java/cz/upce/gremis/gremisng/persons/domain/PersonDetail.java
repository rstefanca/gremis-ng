package cz.upce.gremis.gremisng.persons.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.jetbrains.annotations.Nullable;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Value
@Builder
@With
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PersonDetail {

    static final PersonDetail EMPTY = PersonDetail.builder()
            .firstName("N/A")
            .lastName("N/A")
            .build();

    //@formatter:off

    @Nullable String degreeLeading;

    String firstName;

    String lastName;

    @Nullable String degreeTrailing;

    @Nullable String workplace;

    @Nullable String employer;

    @Nullable String note;

    @Nullable String job;

    Gender gender;

    //@formatter:on

    @JsonIgnore
    public String getFullName() {
        return getFullName(false);
    }

    public String getFullName(boolean excludeDegrees) {
        var degreeLeading = !excludeDegrees ? getDegreeLeading() : null;
        var degreeTrailing = !excludeDegrees ? getDegreeTrailing() : null;
        return ("%s %s %s %s".formatted(
                defaultIfNull(degreeLeading, ""),
                getFirstName(),
                getLastName(),
                defaultIfNull(degreeTrailing, "")
        )).trim();
    }
}
