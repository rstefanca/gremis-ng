package cz.upce.gremis.gremisng.organization.domain;


import cz.upce.gremis.gremisng.common.PerRollamApproveType;
import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.time.DateInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@Table(name = "BOARD")
public class Board extends AbstractEntity {

    private static final Set<BoardOption> ALL_OPTIONS = EnumSet.allOf(BoardOption.class);

    //formatter:off
    @SuppressWarnings("unused")
    /*@Embedded
    private final AuditAttributes auditAttributes = new AuditAttributes();*/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_gen")
    @SequenceGenerator(name = "board_gen", sequenceName = "BOARD_ID_seq", allocationSize = 5, initialValue = 1000)
    @Getter
    private Long id;

    private @NonNull
    @Getter
    @Setter
    String name;

    private @NonNull
    @Getter
    @Setter
    String abbreviation;

    @NonNull
    @Getter
    @Setter
    @Column(name = "TERM_TYPE")
    private TermType termType;

    @Column(name = "PER_ROLLAM_ENABLED")
    @Getter
    @Setter
    private boolean perRollam;

    @Column(name = "COMMITTEES_ENABLED")
    @Getter
    @Setter
    private boolean committeesEnabled;

    @Column(name = "PER_ROLLAM_APPROVING_TYPE")
    @Getter
    @Setter
    private PerRollamApproveType perRollamApproveType = PerRollamApproveType.NONE;

    @Column(name = "MINIMUM_MEMBER_COUNT")
    private @Getter
    @Setter int minMemberCount;

    /*@Convert(converter = HashMapConverter.class)
    @Column(name = "OTHER_RECIPIENTS")
    private @Getter
    @Setter Map<String, Object> otherRecipients;*/

    @Getter @Setter
    private boolean active = true;


    //formatter:on

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @Getter
    private final Collection<BoardTerm> terms = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @Getter
    private final Collection<BoardSeat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @Getter
    private final Collection<Committee> committees = new ArrayList<>();

    /**
     * Konstruktor urceny primarne pro hibernate a mapper (napr. MapStruct).
     *
     */
    public Board() {
    }


    public void add(BoardTerm boardTerm) {
        if (termType == TermType.MEMBER && !terms.isEmpty()) {
            throw new TermExistsError();
        }

        if (termType == TermType.BOARD && checkOverlappingTerms(boardTerm)) {
            throw new TermOverlapError();
        }

        terms.add(boardTerm);
        boardTerm.setBoard(this);
    }

    private boolean checkOverlappingTerms(BoardTerm other) {
        return terms.stream().anyMatch(term -> term.overlaps(other));
    }

    public void add(BoardSeat boardSeat) {
        seats.add(boardSeat);
        boardSeat.setBoard(this);
    }

    public void add(Committee committee) {
       if (committeesEnabled) {
           committees.add(committee);
           committee.setBoard(this);
       }
    }

    public Board setOptions(Collection<BoardOption> options) {
        perRollam = options.contains(BoardOption.PER_ROLLAM);
        committeesEnabled = options.contains(BoardOption.COMMITTEES);
        return this;
    }

    @Transient
    public Set<BoardOption> getOptions() {
        if (perRollam && committeesEnabled) {
            return ALL_OPTIONS;
        }

        return Stream.of(
                        ImmutablePair.of(perRollam, BoardOption.PER_ROLLAM),
                        ImmutablePair.of(committeesEnabled, BoardOption.COMMITTEES))
                .filter(it -> it.left)
                .map(it -> it.right)
                .collect(Collectors.toSet());
    }


    public boolean remove(BoardTerm term) {
        return terms.remove(term);
    }

    @Override
    public String toString() {
        return """
                Board %s - %s (%s) [C:%b,P:%b]"""
                .formatted(name, abbreviation, getId(), committeesEnabled, perRollam);
    }
}