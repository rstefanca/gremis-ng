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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_role_gen")
    @SequenceGenerator(name = "system_role_gen", sequenceName = "SYSTEM_ROLE_ID_seq", allocationSize = 15)
    private Long id;

    private @Getter @Setter Long boardId;


    private @Getter @Setter GremisRole permissionLevel = GremisRole.GUEST;

    private @Getter @Setter RoleOrigin roleOrigin;

    @Column(name = "START_DATE")
    private @Getter @Setter LocalDate start;

    @Column(name = "END_DATE")
    private @Getter @Setter LocalDate end;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;



    @Override
    public Long getId() {
        return id;
    }
}