package cz.upce.gremis.gremisng.organization.domain;


import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.time.DateInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "BOARD_TERM")
public class BoardTerm extends AbstractEntity implements DateInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_term_gen")
    @SequenceGenerator(name = "board_term_gen", sequenceName = "BOARD_TERM_ID_SEQ", allocationSize = 15, initialValue = 1000)
    private @Getter Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", updatable = false, nullable = false)
    private @Getter Board board;

    @Column(name = "START_DATE")
    private @NonNull
    @Getter
    @Setter LocalDate start;

    @Column(name = "END_DATE")
    private @NonNull
    @Getter
    @Setter LocalDate end;

    private @NonNull
    @Getter
    @Setter String description;

    void setBoard(Board board) {
        this.board = board;
    }


    @Override
    public String toString() {
        return """
                Board term (%s) [%s] %s - %s""".formatted(getId(), description, start, end);
    }
}
