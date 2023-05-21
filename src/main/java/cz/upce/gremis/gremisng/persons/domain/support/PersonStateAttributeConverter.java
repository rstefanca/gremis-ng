package cz.upce.gremis.gremisng.persons.domain.support;



import cz.upce.gremis.gremisng.persons.domain.PersonState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PersonStateAttributeConverter implements AttributeConverter<PersonState, String> {

    @Override
    public String convertToDatabaseColumn(PersonState attribute) {
        return attribute == null ? null : attribute.name().substring(0, 1);
    }

    @Override
    public PersonState convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "I" -> PersonState.INITIALIZED;
            case "B" -> PersonState.BLOCKED;
            case "A" -> PersonState.ACTIVE;
            case "W" -> PersonState.WAITING;
            default -> throw new IllegalArgumentException("Unknown person state " + dbData);
        };
    }
}

