package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.common.GremisRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SeatSettings {

    //@formatter:off
    @Column(name = "MANDATORY")
    private  @Getter @Setter Filling filling; //jak s tim pracovat? pouze pro UI?

    @Column(name = "MULTIPLE")
    private  @Getter @Setter AssignmentType assignmentType;

    @Column(name = "SYSTEM_ROLE")
    private @Getter @Setter GremisRole grantedRole;

    @Column(name = "MINIMUM_MEMBER_COUNT")
    private @Getter @Setter int minMemberCount;

    //@formatter:on


    @Override
    public String toString() {
        return """
                Settings: [F:%s,T:%s,R:%s]""".formatted(filling, assignmentType, grantedRole);
    }
}