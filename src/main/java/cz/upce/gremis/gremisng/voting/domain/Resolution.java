package cz.upce.gremis.gremisng.voting.domain;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "RESOLUTION")
public class Resolution extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resolution_gen")
    @SequenceGenerator(name = "resolution_gen", sequenceName = "RESOLUTION_ID_SEQ", allocationSize = 25)
    @Getter
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOTE_ID", updatable = false, nullable = false)
    private @Getter @Setter Vote vote;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    @Enumerated(EnumType.STRING)
    private ResolutionResult result = ResolutionResult.NONE;

    @Getter
    @Setter
    @NonNull
    private String text;

    @Getter
    @Setter
    @Nullable
    private String note;

    @Getter
    @Setter
    @Column(name = "R_ORDER")
    private int order = 1;

}
