package cz.upce.gremis.gremisng.persons.domain.support;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.upce.gremis.gremisng.persons.domain.*;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2

@Component
@Order(1)
@RequiredArgsConstructor
@Deprecated
class PersonDataInitializer  {

    private final PersonRepository repository;

    @Value("classpath:persons.json")
    private Resource importResource;



    @SneakyThrows
    @PostConstruct
    @Transactional
    public void initialize() {

        log.info("Creating test user (Member)");
        var testUser = new Person("tester", "test-gremis@upce.cz", "Test", "Testovic", Gender.M);
        testUser.setPassword("heslo");
        repository.save(testUser);

        log.info("Importing persons");

        if (importResource.exists()) {

            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Collection<PersonRow> persons = objectMapper.readValue(importResource.getInputStream(),
                    PersonImport.class).data;


            Map<Long, List<PersonRow>> personsById = persons.stream()
                    .collect(Collectors.groupingBy(PersonRow::getPersonId));

            personsById.forEach((key, items) -> {
                List<PersonHistory.HistoryRecord> records = items.stream()
                        .sorted(Comparator.comparing(PersonRow::getValidFrom))
                        .map(it -> new PersonHistory.HistoryRecord(
                                PersonDetail.builder()
                                        .firstName(it.getName())
                                        .lastName(it.getSurname())
                                        .degreeLeading(it.getDegreeLeading())
                                        .degreeTrailing(it.getDegreeTrailing())
                                        .employer(it.getEmployer())
                                        .workplace(it.getWorkplace())
                                        .job(it.getJob())
                                        .note(it.getNote())
                                        .gender(Gender.NA)
                                        .build(),
                                LocalDate.from(it.validFrom),
                                null
                        )).collect(Collectors.toList());

                PersonHistory personHistory = new PersonHistory(records);
                var currentDetail = personHistory.getCurrentDetail();
                Person person = new Person();

                person.setLogin(normalizeString(currentDetail.getFirstName().charAt(0) + currentDetail.getLastName()));
                person.setPassword("hesloveslo");
                PersonRow personRow = items.get(0);
                person.setEmail(Optional.ofNullable(personRow.email).orElse(normalizeString(personRow.name + personRow.surname) + "@upce.cz"));
                person.setState(personRow.state);
                person.setAuthenticationMethod(personRow.authenticationMethod);
                person.setPersonHistory(personHistory);

                repository.save(person);


            });
        }


    }

    private static String normalizeString(final String string) {
        if (string == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        String temp = string.trim();
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.replaceAll("[\uFEFF-\uFFFF]", ""); // remove 'ZERO WIDTH NO-BREAK SPACE' chars (U+FEFF)

        return temp.toLowerCase();
    }

    @Data
    static class PersonImport {
        Collection<PersonRow> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PersonRow {
        @JsonProperty("NAME")
        String name;

        @JsonProperty("SURNAME")
        String surname;

        @JsonProperty("PERSON_ID")
        long personId;

        @JsonProperty("ACADEMIC_DEGREE_LEADING")
        String degreeLeading;

        @JsonProperty("ACADEMIC_DEGREE_TRAILING")
        String degreeTrailing;

        @JsonProperty("EMPLOYER")
        String employer;

        @JsonProperty("WORKPLACE_NAME")
        String workplace;

        @JsonProperty("JOB")
        String job;

        @JsonProperty("NOTE")
        String note;

        @JsonProperty("VALID_FROM")
        LocalDate validFrom;

        @JsonProperty("AUTHENTICATION_METHOD")
        AuthenticationMethod authenticationMethod;

        @JsonProperty("STATE")
        PersonState state;

        @JsonProperty("EMAIL")
        String email;
    }
}
