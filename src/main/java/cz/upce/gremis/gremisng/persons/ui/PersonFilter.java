package cz.upce.gremis.gremisng.persons.ui;


import cz.upce.gremis.gremisng.persons.domain.Person;

public class PersonFilter {
    private String searchTerm;

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean test(Person person) {
        boolean matchesFullName = matches(person.getFullName(), searchTerm);
        boolean matchesProfession = matches(person.getEmployer(), searchTerm);
        return matchesFullName || matchesProfession;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}