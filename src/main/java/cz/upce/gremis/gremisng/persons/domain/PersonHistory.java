package cz.upce.gremis.gremisng.persons.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class PersonHistory {

    public static PersonHistory EMPTY = new PersonHistory(Collections.emptyList());

    LinkedList<HistoryRecord> records;

    @JsonCreator
    public PersonHistory(@JsonProperty(value = "records") Collection<HistoryRecord> records) {
        this.records = new LinkedList<>(records);
    }

    private PersonHistory(LinkedList<HistoryRecord> records) {
        this.records = records;
    }

    static PersonHistory create(PersonDetail personDetail) {
        LinkedList<HistoryRecord> records = new LinkedList<>();
        records.add(HistoryRecord.latest(personDetail));
        return new PersonHistory(records);
    }

    @JsonIgnore
    public PersonDetail getCurrentDetail() {
        if (records.isEmpty()) {
            return PersonDetail.EMPTY;
        }
        return records.getLast().getValue();
    }

    public PersonHistory addPersonDetail(PersonDetail next) {
        LinkedList<HistoryRecord> newRecords = new LinkedList<>(records);

        if(!records.isEmpty()) {
            var last = records.getLast();
            last.close();
        }

        newRecords.add(HistoryRecord.latest(next));

        return new PersonHistory(newRecords);
    }

    public boolean isLastValidFrom(LocalDate date) {
        return records.getLast().validFrom.equals(date);
    }

    public PersonHistory replaceLast(PersonDetail personDetail) {
        var current = new LinkedList<>(records);
        current.removeLast();
        return new PersonHistory(current).addPersonDetail(personDetail);
    }

    public Stream<HistoryRecord> records() {
        return records.stream();
    }

    public int size() {
        return records.size();
    }

    public Optional<PersonDetail> getDetailByDate(LocalDate localDate) {
        return records.stream()
                .filter(it -> it.containsDate(localDate))
                .map(HistoryRecord::getValue)
                .findFirst();
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class HistoryRecord {
        PersonDetail value;
        LocalDate validFrom;
        LocalDate validTo;

        static HistoryRecord latest(PersonDetail personDetail) {
            return new HistoryRecord(personDetail, LocalDate.now(), null);
        }

        boolean containsDate(LocalDate localDate) {
            if (validTo == null && localDate.isAfter(validFrom)) {
                return true;
            }

            return localDate.isAfter(validFrom) && localDate.isBefore(validTo);
        }

        private void close() {
            validTo = LocalDate.now();
        }
    }
}
