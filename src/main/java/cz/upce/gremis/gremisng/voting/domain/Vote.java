package cz.upce.gremis.gremisng.voting.domain;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.core.EntityCreationListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "VOTE")
@EntityListeners({EntityCreationListener.class, AuditingEntityListener.class})
public class Vote extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_gen")
    @SequenceGenerator(name = "vote_gen", sequenceName = "VOTE_ID_SEQ", allocationSize = 15)
    @Getter
    private Long id;

    @Column(updatable = false, nullable = false)
    @Getter
    @Setter
    private Long boardTermId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private VoteBoard board;

    @Getter
    @Setter
    @NotNull
    private String name;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private VoteState voteState = VoteState.DRAFT;

    @Getter
    @Setter
    private LocalDateTime notifyAt;

    @Getter
    @Setter
    private String cancelRationale;

    @Setter
    @Getter
    @Nullable
    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    @Getter
    @Setter
    private LocalDateTime endTime;

    @Getter
    @Setter
    @Column(columnDefinition = "CLOB NOT NULL", nullable = false)
    private String coverLetter;

    @Getter
    @Setter
    @Column(columnDefinition = "CLOB")
    private String note;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Getter
    private final Collection<Resolution> resolutions = new ArrayList<>();

    public VoteBoard getBoard() {
        return board;
    }

    public void setBoard(VoteBoard board) {
        this.board = board;
    }

    public void addResolution(Resolution resolution) {
        resolution.setVote(this);
        resolutions.add(resolution);
    }

    public void removeResolution(Resolution resolution) {
        resolutions.remove(resolution);
    }

    public void publish(LocalDateTime startTime) {

    }

}
