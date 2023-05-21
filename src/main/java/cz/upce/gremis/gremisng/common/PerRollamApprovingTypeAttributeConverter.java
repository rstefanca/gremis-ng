package cz.upce.gremis.gremisng.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class PerRollamApprovingTypeAttributeConverter implements AttributeConverter<PerRollamApproveType, String> {

    @Override
    public String convertToDatabaseColumn(PerRollamApproveType attribute) {
        return attribute.name().substring(0, 1);
    }

    @Override
    public PerRollamApproveType convertToEntityAttribute(String dbData) {
        if (dbData==null)
            return PerRollamApproveType.NONE;
        return switch (dbData) {
            case "N" -> PerRollamApproveType.NONE;
            case "I" -> PerRollamApproveType.INTERNAL;
            case "E" -> PerRollamApproveType.EXTERNAL;
            default -> throw new IllegalArgumentException("Unknown per rollam attribute type " + dbData);
        };
    }
}
