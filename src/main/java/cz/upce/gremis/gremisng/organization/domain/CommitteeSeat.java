package cz.upce.gremis.gremisng.organization.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "COMMITTEE_SEAT")
@EntityListeners({CommitteeSeatListener.class})
public class CommitteeSeat extends AbstractSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "committee_seat_gen")
    @SequenceGenerator(name = "committee_seat_gen", sequenceName = "COMMITTEE_SEAT_ID_SEQ", allocationSize = 15)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMITTEE_TEMPLATE_ID", updatable = false, nullable = false)
    private @Getter @Setter Committee committee;

    @Override
    public Long getId() {
        return id;
    }
}
