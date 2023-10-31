package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.common.GremisRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeatSettings {

    @Enumerated(EnumType.STRING)
    @Column(name = "MANDATORY")
    private Filling filling; //jak s tim pracovat? pouze pro UI?

    @Enumerated(EnumType.STRING)
    @Column(name = "MULTIPLE")
    private AssignmentType assignmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "SYSTEM_ROLE")
    private GremisRole grantedRole;

    @Column(name = "MINIMUM_MEMBER_COUNT")
    private int minMemberCount;

    @Override
    public String toString() {
        return "Settings: [F:%s,T:%s,R:%s]".formatted(filling, assignmentType, grantedRole);
    }
}