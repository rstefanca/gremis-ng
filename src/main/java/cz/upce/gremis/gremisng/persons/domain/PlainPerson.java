package cz.upce.gremis.gremisng.persons.domain;


import cz.upce.gremis.gremisng.persons.domain.support.PersonDetailAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.util.Objects;

/**
 * Simplified read-only {@link Person} entity.
 *
 * @see Person
 */
@Entity
@Immutable
@Table(name = "PERSON")
public class PlainPerson  {

    @Id
    private Long id;

    @Convert(converter = PersonDetailAttributeConverter.class)
    @Column(name = "HISTORY", columnDefinition = "clob", updatable = false, insertable = false)
    private PersonDetail personDetail;

    @Getter
    private String email;

    @Deprecated
    protected PlainPerson() {

    }


    public String getDegreeLeading() {
        return personDetail.getDegreeLeading();
    }

    public String getFirstName() {
        return personDetail.getFirstName();
    }

    public String getLastName() {
        return personDetail.getLastName();
    }

    public String getDegreeTrailing() {
        return personDetail.getDegreeTrailing();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainPerson that = (PlainPerson) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
