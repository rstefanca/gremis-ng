package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "COMMITTEE_TEMPLATE")
@EntityListeners({CommitteeListener.class})
public class Committee extends AbstractEntity {
    //@formatter:off
    //private final @Embedded AuditAttributes auditAttributes = new AuditAttributes();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "committee_template_gen")
    @SequenceGenerator(name = "committee_template_gen", sequenceName = "COMMITTEE_TEMPLATE_ID_SEQ", allocationSize = 15)
    private @Getter Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOARD_ID", updatable = false, nullable = false)
    private @Getter Board board;

    private @Getter @Setter String name;

    private @Getter @Setter String abbreviation;

    private @Getter @Setter boolean mandatory;

    @Column(name = "MINIMUM_MEMBER_COUNT")
    private @Getter @Setter int minimumSeats;

    private @Getter @Setter boolean workingGroupsEnabled;

    @Column(name = "TERM_DURATION")
    private @Getter @Setter int termDurationInMonths;

    private @Getter @Setter boolean active = true;

    @Column(name = "PRIORITY_ORDER")
    private @Getter @Setter int order = 1;



    //@formatter:on

    @OneToMany(
            mappedBy = "committee",
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private @Getter Collection<CommitteeSeat> seats = new ArrayList<>();


    public Committee() {
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}