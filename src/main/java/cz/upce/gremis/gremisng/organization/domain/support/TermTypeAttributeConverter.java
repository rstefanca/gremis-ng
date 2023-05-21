package cz.upce.gremis.gremisng.organization.domain.support;

import cz.upce.gremis.gremisng.organization.domain.TermType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class TermTypeAttributeConverter implements AttributeConverter<TermType, String> {

    @Override
    public String convertToDatabaseColumn(TermType attribute) {
        return attribute.name().substring(0, 1);
    }

    @Override
    public TermType convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "B" -> TermType.BOARD;
            case "M" -> TermType.MEMBER;
            default -> throw new IllegalArgumentException("Unknown term type " + dbData);
        };
    }
}