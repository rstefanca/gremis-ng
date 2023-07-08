package cz.upce.gremis.gremisng.common;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
class GremisRoleAttributeConverter implements AttributeConverter<GremisRole, String> {

    @Override
    public String convertToDatabaseColumn(GremisRole attribute) {
        if (attribute == null) {
            return null;
        }
        return asDbValue(attribute);
    }

    @Override
    public GremisRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(GremisRole.values())
                .filter(it -> asDbValue(it).equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown role value: " + dbData));
    }

    private static String asDbValue(GremisRole attribute) {
        return attribute.name().substring(0,1);
    }
}
