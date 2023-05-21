package cz.upce.gremis.gremisng.persons.domain.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cz.upce.gremis.gremisng.persons.domain.PersonHistory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PersonHistoryAttributeConverter implements AttributeConverter<PersonHistory, String> {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(PersonHistory attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PersonHistory convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return PersonHistory.EMPTY;
        }

        try {
            return mapper.readValue(dbData, PersonHistory.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
