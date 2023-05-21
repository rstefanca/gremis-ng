package cz.upce.gremis.gremisng.persons.domain.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cz.upce.gremis.gremisng.persons.domain.PersonDetail;
import cz.upce.gremis.gremisng.persons.domain.PersonHistory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

@Converter
public class PersonDetailAttributeConverter implements AttributeConverter<PersonDetail, String> {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(PersonDetail attribute) {
        throw new UnsupportedOperationException("Conversion to database column not supported");
    }

    @Override
    @SneakyThrows
    public PersonDetail convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        PersonHistory personHistory = mapper.readValue(dbData, PersonHistory.class);
        return personHistory.getCurrentDetail();

    }
}
