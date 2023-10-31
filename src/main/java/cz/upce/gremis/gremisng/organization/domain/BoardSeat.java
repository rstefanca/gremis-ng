package cz.upce.gremis.gremisng.organization.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BOARD_SEAT")
//@EntityListeners({BoardSeatListener.class})
public class BoardSeat extends AbstractSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seat_gen")
    @SequenceGenerator(name = "board_seat_gen", sequenceName = "BOARD_SEAT_ID_seq", allocationSize = 15)
    private @Getter Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", updatable = false, nullable = false)
    private @Getter
    @Setter Board board;

    @Column(name = "TERM_DURATION")
    private @Getter
    @Setter Integer duration;
}
