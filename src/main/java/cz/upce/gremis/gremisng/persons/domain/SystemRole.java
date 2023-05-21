package cz.upce.gremis.gremisng.persons.domain;

import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.time.DateInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "SYSTEM_ROLE")
public class SystemRole extends AbstractEntity implements DateInterval {

    //@formatter:off
    //private @Getter @Embedded AuditAttributes auditAttributes = new AuditAttributes();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_role_gen")
    @SequenceGenerator(name = "system_role_gen", sequenceName = "SYSTEM_ROLE_ID_seq", allocationSize = 15)
    private Long id;

    private @Getter @Setter Long boardId;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", updatable = false, nullable = false)
    @Getter
    @Setter
    private Person person;

    private @Getter @Setter GremisRole permissionLevel = GremisRole.GUEST;

    private @Getter @Setter RoleOrigin roleOrigin;

    @Column(name = "START_DATE")
    private @Getter @Setter LocalDate start;

    @Column(name = "END_DATE")
    private @Getter @Setter LocalDate end;

    //@formatter:on


    @Override
    public Long getId() {
        return id;
    }
}