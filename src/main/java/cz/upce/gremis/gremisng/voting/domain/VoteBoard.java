package cz.upce.gremis.gremisng.voting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "BOARD")
@Entity
@Getter
@NoArgsConstructor
public class VoteBoard {

    @Id
    private long id;

    @Column
    private String name;

    public VoteBoard(long id) {
        this.id = id;
    }
}
