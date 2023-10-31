package cz.upce.gremis.gremisng.meetings.domain;

import cz.upce.gremis.gremisng.common.AuditAttributes;
import cz.upce.gremis.gremisng.core.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "MEETING")
public class Meeting extends AbstractEntity {

    @Embedded
    private final AuditAttributes auditAttributes = new AuditAttributes();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meeting_gen")
    @SequenceGenerator(name = "meeting_gen", sequenceName = "MEETING_ID_seq", allocationSize = 5, initialValue = 1000)
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long boardId;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long boardTermId;

    @Getter
    @Setter
    private String note;

    @Column(name = "MEETING_TYPE")
    private @Getter @Setter MeetingType type;

    @Getter
    @Setter
    @Nullable
    @Column(length = 1000)
    private String url;

}
