package cz.upce.gremis.gremisng.persons.domain;



import cz.upce.gremis.gremisng.core.AbstractEntity;
import cz.upce.gremis.gremisng.persons.domain.support.PersonHistoryAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Entity
@Table(name = "PERSON")
public class Person extends AbstractEntity {

    private transient boolean isNew = true;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_gen")
    @SequenceGenerator(name = "person_gen", sequenceName = "PERSON_ID_SEQ", allocationSize = 15)
    private Long id;

    @Column(unique=true)
    private @Getter @Setter String login;

    @Column(unique=true)
    private @Getter @Setter String email;

    @Convert(converter = PersonHistoryAttributeConverter.class)
    @Column(name = "HISTORY", columnDefinition = "clob")
    private PersonHistory personHistory;

    private @Getter @Setter String password;

    @NonNull
    @Getter
    @Setter
    @Column(name = "AUTHENTICATION_METHOD", columnDefinition = "varchar(1)")
    private AuthenticationMethod authenticationMethod;

    @NonNull
    @Getter
    @Setter
    @Column(name = "STATE", columnDefinition = "varchar(1)")
    private PersonState state;

    @Column(name = "INVITED")
    private @Getter @Setter LocalDate invited;

    @Column(name = "BLOCKED")
    private @Getter @Setter LocalDate blocked;

    @Column(name = "RESET_TOKEN")
    private @Getter @Setter String resetToken;

    @Column(name = "INVITE_TOKEN")
    private @Getter @Setter String inviteToken;

    @Column(name = "RESET_EXPIRED")
    private @Getter @Setter LocalDate resetExpired;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SystemRole> systemRoles = new LinkedHashSet<>();

    public Person() {
    }

    public Person(String login, String email, String firstName, String lastName, Gender sex) {
        this.login = login;
        this.email = email;
        var personDetail = PersonDetail.builder().firstName(firstName).lastName(lastName).gender(sex).build();
        this.personHistory = PersonHistory.create(personDetail);
        this.currentDetail = personDetail;
        this.setState(PersonState.INITIALIZED);
        this.setAuthenticationMethod(AuthenticationMethod.NONE);
    }

    public Person(PersonDetail personDetail) {
        this.personHistory = PersonHistory.create(personDetail);
        this.currentDetail = personDetail;
    }

    @Override
    public Long getId() {
        return id;
    }


    @Override
    public boolean isNew() {
        return isNew;
    }

    @Transient
    private PersonDetail currentDetail;

    public Set<SystemRole> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(Set<SystemRole> systemRoles) {
        this.systemRoles = systemRoles;
    }


    public String getDegreeLeading() {
        return currentDetail.getDegreeLeading();
    }

    public String getFirstName() {
        return currentDetail.getFirstName();
    }

    public String getLastName() {
        return currentDetail.getLastName();
    }

    public String getDegreeTrailing() {
        return currentDetail.getDegreeTrailing();
    }

    public Gender getGender() {
        return currentDetail.getGender();
    }

    public String getWorkplace() {
        return currentDetail.getWorkplace();
    }

    public String getEmployer() {
        return currentDetail.getEmployer();
    }

    public String getJob() {
        return currentDetail.getJob();
    }

    public String getNote() {
        return currentDetail.getNote();
    }

    @Deprecated
    public Stream<PersonHistory.HistoryRecord> getPersonHistory() {
        return personHistory.records();
    }

    public PersonHistory getHistory() {
        return personHistory;
    }

    public void addPersonDetail(PersonDetail personDetail) {
        if (!currentDetail.equals(personDetail)) {
            personHistory = personHistory.addPersonDetail(personDetail);
            currentDetail = personDetail;
        }
    }

    public void replaceLastPersonDetail(PersonDetail personDetail) {
        personHistory = personHistory.replaceLast(personDetail);
    }

    public Optional<PersonDetail> getDetailByDate(LocalDate localDate) {
        return personHistory.getDetailByDate(localDate);
    }

    @PostLoad
    void init() {
        currentDetail = personHistory.getCurrentDetail();
    }

    public void setPersonHistory(PersonHistory personHistory) {
        this.personHistory = personHistory;
    }

    @Deprecated
    void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return """
                Person %s(%s)""".formatted(login, getId());
    }

    public String getFullName() {
        return currentDetail.getFullName();
    }

    public String getProfession() {
        return getEmployer();
    }
}
