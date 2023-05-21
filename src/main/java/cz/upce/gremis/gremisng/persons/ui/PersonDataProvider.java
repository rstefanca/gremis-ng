package cz.upce.gremis.gremisng.persons.ui;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PersonDataProvider extends AbstractBackEndDataProvider<Person, PersonFilter> {
    private final PersonRepository personRepository;

    public PersonDataProvider(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    protected Stream<Person> fetchFromBackEnd(Query<Person, PersonFilter> query) {
        // A real app should use a real database or a service
        // to fetch, filter and sort data.
        var pageable = Pageable.ofSize(query.getPageSize()).withPage(query.getPage());
        var page = personRepository.findAll(pageable);
        Stream<Person> stream = page.stream();

        // Filtering
        if (query.getFilter().isPresent()) {
            stream = stream.filter(person -> query.getFilter().get().test(person));
        }

        // Sorting
        if (query.getSortOrders().size() > 0) {
            stream = stream.sorted(sortComparator(query.getSortOrders()));
        }

        // Pagination
        return page.stream();
    }

    @Override
    protected int sizeInBackEnd(Query<Person, PersonFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    private static Comparator<Person> sortComparator(List<QuerySortOrder> sortOrders) {
        return sortOrders.stream().map(sortOrder -> {
            Comparator<Person> comparator = personFieldComparator(sortOrder.getSorted());

            if (sortOrder.getDirection() == SortDirection.DESCENDING) {
                comparator = comparator.reversed();
            }

            return comparator;
        }).reduce(Comparator::thenComparing).orElse((p1, p2) -> 0);
    }

    private static Comparator<Person> personFieldComparator(String sorted) {
        if (sorted.equals("name")) {
            return Comparator.comparing(Person::getFullName);
        } else if (sorted.equals("profession")) {
            return Comparator.comparing(Person::getProfession);
        }
        return (p1, p2) -> 0;
    }
}