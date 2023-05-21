package cz.upce.gremis.gremisng.persons.ui;



import cz.upce.gremis.gremisng.persons.domain.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

class PersonViewBean {

    private final Person delegate;


    private String degreeLeading;

    private String firstName;

    private String lastName;

    private @Nullable String degreeTrailing;

    private @Nullable String workplace;

    private @Nullable String employer;

    private @Nullable String note;

    private @Nullable String job;

    private Gender gender;

    private PersonDetail personDetail = null;

    public PersonViewBean(Person delegate) {
        this.delegate = delegate;
    }

    public String getLogin() {
        return delegate.getLogin();
    }

    public String getEmail() {
        return delegate.getEmail();
    }

    public Person setEmail(String email) {
        return delegate.setEmail(email);
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return delegate.getAuthenticationMethod();
    }

    public Person setInviteToken(String inviteToken) {
        return delegate.setInviteToken(inviteToken);
    }

    public Person setInvited(LocalDate invited) {
        return delegate.setInvited(invited);
    }

    public PersonState getState() {
        return delegate.getState();
    }

    public LocalDate getResetExpired() {
        return delegate.getResetExpired();
    }

    public LocalDate getInvited() {
        return delegate.getInvited();
    }

    public LocalDate getBlocked() {
        return delegate.getBlocked();
    }

    public String getResetToken() {
        return delegate.getResetToken();
    }


    public Person setState(PersonState state) {
        return delegate.setState(state);
    }

    public Person setBlocked(LocalDate blocked) {
        return delegate.setBlocked(blocked);
    }

    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        delegate.setAuthenticationMethod(authenticationMethod);
    }


    public Person setLogin(String login) {
        return delegate.setLogin(login);
    }


    public String getDegreeLeading() {
        return delegate.getDegreeLeading();
    }

    public String getFirstName() {
        return delegate.getFirstName();
    }

    public void setFirstName(String firstName) {
       if (personDetail == null) {
           var currentDetail = delegate.getHistory().getCurrentDetail();
           var newDetail = currentDetail.withFirstName(firstName);
           if (!newDetail.equals(currentDetail)) {
               delegate.addPersonDetail(personDetail);
           }
       }
    }

    public String getLastName() {
        return delegate.getLastName();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDegreeTrailing() {
        return delegate.getDegreeTrailing();
    }

    public Gender getGender() {
        return delegate.getGender();
    }

    public String getWorkplace() {
        return delegate.getWorkplace();
    }

    public String getEmployer() {
        return delegate.getEmployer();
    }

    public String getJob() {
        return delegate.getJob();
    }

    public String getNote() {
        return delegate.getNote();
    }


    public String getProfession() {
        return delegate.getProfession();
    }

    PersonDetail getPersonDetail() {

        return PersonDetail.builder()
                .firstName(firstName)
                .lastName(lastName)
                .employer(employer)
                .workplace(workplace)
                .job(job)
                .note(note)
                .build();
    }

    public void setDegreeLeading(String degree) {

    }

    public void setDegreeTrailing(String degree) {

    }

    public void setEmployer(String employer) {

    }

    public void setWorkplace(String workplace) {

    }

    public void setJob(String job) {

    }

    public void setNote(String note) {

    }

    public void setGender(Gender gender) {

    }
}
