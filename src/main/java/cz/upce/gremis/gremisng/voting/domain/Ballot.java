package cz.upce.gremis.gremisng.voting.domain;

import cz.upce.gremis.gremisng.common.AuditAttributes;
import cz.upce.gremis.gremisng.persons.domain.Person;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BALLOT")
public class Ballot {

    @SuppressWarnings("unused")
    @Embedded
    private final AuditAttributes auditAttributes = new AuditAttributes();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ballot_gen")
    @SequenceGenerator(name = "ballot_gen", sequenceName = "BALLOT_ID_SEQ", allocationSize = 15)
    @Getter
    private Long id;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", updatable = false, nullable = false)
    @Getter
    private Person person;

    @Getter
    private String note;

    @Setter(value = AccessLevel.PACKAGE)
    private String ipAddress;


    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name="\"option\"")
    private BallotOption option = BallotOption.NONE;

    @JoinColumn(updatable = false, nullable = false)
    private Long voteId;
}
