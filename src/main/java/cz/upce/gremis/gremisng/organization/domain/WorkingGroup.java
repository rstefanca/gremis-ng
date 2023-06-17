package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "WORKING_GROUP")
public class WorkingGroup extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "working_group_gen")
    @SequenceGenerator(name = "working_group_gen", sequenceName = "WORKING_GROUP_ID_SEQ", allocationSize = 15)
    private Long id;

    @Column(name = "COMMITTEE_TERM_ID", nullable = false, updatable = false)
    @Getter
    @Setter
    private Long committeeTermId;

    @Column(name = "START_DATE", nullable = false)
    private @NonNull
    @Getter
    @Setter LocalDate start;

    @Column(name = "END_DATE", nullable = false)
    private @NonNull @Getter @Setter LocalDate end;

    @Column(name = "NAME", nullable = false)
    private @NonNull @Getter @Setter String name;


    @Override
    public Long getId() {
        return null;
    }
}
