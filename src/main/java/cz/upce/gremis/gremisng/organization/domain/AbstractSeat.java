package cz.upce.gremis.gremisng.organization.domain;


import cz.upce.gremis.gremisng.core.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractSeat extends AbstractEntity {

    @NonNull
    @Getter
    @Setter
    private String name;


    @Embedded
    @Getter
    @Setter
    private SeatSettings settings = new SeatSettings();

    @Column(name = "PRIORITY_ORDER")
    private @Getter @Setter int order;

    /*
    @Column(name = "ACTIVE") //true/false
    private @Getter @Setter EntityState state;
    */



}
