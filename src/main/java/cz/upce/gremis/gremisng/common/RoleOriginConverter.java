package cz.upce.gremis.gremisng.common;

import cz.upce.gremis.gremisng.persons.domain.RoleOrigin;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class RoleOriginConverter implements AttributeConverter<RoleOrigin, String> {

    @Override
    public String convertToDatabaseColumn(RoleOrigin attribute) {
        return attribute.name().substring(0, 1);
    }

    @Override
    public RoleOrigin convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "B" -> RoleOrigin.BOARD;
            case "C" -> RoleOrigin.COMMITTEE;
            case "M" -> RoleOrigin.MANUAL;
            default -> throw new IllegalArgumentException("Unknown role origin: " + dbData);
        };
    }
}