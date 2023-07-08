package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.common.AuditAttributes;
import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.time.DateInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "COMMITTEE_TERM")
public class CommitteeTerm extends AbstractEntity implements DateInterval {

    //@formatter:off
    private final @Embedded AuditAttributes auditAttributes = new AuditAttributes();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "committee_term_gen")
    @SequenceGenerator(name = "committee_term_gen", sequenceName = "COMMITTEE_TERM_ID_SEQ", allocationSize = 15)
    private @Getter Long id;

    @Column(name = "START_DATE")
    private @NonNull @Getter @Setter LocalDate start;

    @Column(name = "END_DATE")
    private @NonNull @Getter @Setter LocalDate end;

    private @NonNull @Getter @Setter String name;

    /*@ManyToOne
    @JoinColumn(name = "BOARD_TERM_ID", updatable = false, nullable = false)
    private @Getter @Setter BoardTerm boardTerm;*/

    private @Getter @Setter Long boardTermId;


    @ManyToOne
    @JoinColumn(name = "COMMITTEE_TEMPLATE_ID", updatable = false, nullable = false)
    private @Getter @Setter Committee committee;


    //@formatter:on



    @Override
    public String toString() {
        return "CommitteeTerm{id=%d, start=%s, end=%s, name='%s'}".formatted(id, start, end, name);
    }

}