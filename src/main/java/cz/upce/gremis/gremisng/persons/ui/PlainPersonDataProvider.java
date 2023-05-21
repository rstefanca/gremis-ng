package cz.upce.gremis.gremisng.persons.ui;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PlainPerson;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PlainPersonDataProvider extends AbstractBackEndDataProvider<PlainPerson, PlainPersonDataProvider.PlainPersonFilter> {

    @Override
    protected Stream<PlainPerson> fetchFromBackEnd(Query<PlainPerson, PlainPersonFilter> query) {
        return null;
    }

    @Override
    protected int sizeInBackEnd(Query<PlainPerson, PlainPersonFilter> query) {
        return 0;
    }


    public static class PlainPersonFilter {
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

}
