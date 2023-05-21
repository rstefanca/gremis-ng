package cz.upce.gremis.gremisng.persons.domain.support;


import cz.upce.gremis.gremisng.persons.domain.AuthenticationMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuthenticationMethodAttributeConverter implements AttributeConverter<AuthenticationMethod, String> {

    @Override
    public String convertToDatabaseColumn(AuthenticationMethod attribute) {
        return attribute == null ? null : attribute.name().substring(0, 1);
    }

    @Override
    public AuthenticationMethod convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "N" -> AuthenticationMethod.NONE;
            case "E" -> AuthenticationMethod.EXTERNAL;
            case "L" -> AuthenticationMethod.LOCAL;
            default -> throw new IllegalArgumentException("Unknown person state " + dbData);
        };
    }
}

