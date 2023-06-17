package cz.upce.gremis.gremisng.assignments.domain;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.persons.domain.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractAssignment extends AbstractEntity {

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private @NonNull String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", updatable = false, nullable = false)
    private @NonNull @Getter @Setter Person person;

    @Column(name = "START_DATE")
    private @NonNull @Getter @Setter LocalDate start;

    @Column(name = "END_DATE")
    private @NonNull @Getter @Setter LocalDate end;

    private @Nullable
    @Getter @Setter String note;

   /* @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SYSTEM_ROLE_ID")
    @Getter
    @Setter
    @Nullable
    private SystemRole systemRole;*/

}
