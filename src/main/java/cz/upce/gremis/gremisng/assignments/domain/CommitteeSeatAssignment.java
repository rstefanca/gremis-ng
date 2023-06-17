package cz.upce.gremis.gremisng.assignments.domain;

import cz.upce.gremis.gremisng.organization.domain.CommitteeSeat;
import cz.upce.gremis.gremisng.organization.domain.CommitteeTerm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "COMMITTEE_SEAT_ASSIGNMENT")
public class CommitteeSeatAssignment extends AbstractAssignment implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "committee_seat_ass_gen")
    @SequenceGenerator(name = "committee_seat_ass_gen", sequenceName = "COMM_SEAT_ASSIGNMENT_ID_seq", allocationSize = 15)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMITTEE_TERM_ID", updatable = false, nullable = false)
    private CommitteeTerm term;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMITTEE_SEAT_ID", nullable = false, updatable = false)
    private CommitteeSeat seat;

    @Override
    public Long getId() {
        return id;
    }
}
