package cz.upce.gremis.gremisng.assignments.domain;

import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "BOARD_SEAT_ASSIGNMENT")
public class BoardSeatAssignment extends AbstractAssignment implements Persistable<Long> {

    //@formatter:off
    @SuppressWarnings("unused")
    //private final @Embedded AuditAttributes auditAttributes = new AuditAttributes();

    @Transient
    private boolean isNew = false;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "boardSeatAssignment_gen")
    @SequenceGenerator(name = "boardSeatAssignment_gen", sequenceName = "BOARD_SEAT_ASSIGNMENT_ID_seq", allocationSize = 15)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_TERM_ID", updatable = false, nullable = false)
    private @Getter @Setter BoardTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_SEAT_ID", nullable = false)
    private @Getter @Setter BoardSeat seat;

    //@formatter:on



    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}