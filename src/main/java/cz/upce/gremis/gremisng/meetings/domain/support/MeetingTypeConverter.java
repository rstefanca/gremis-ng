package cz.upce.gremis.gremisng.meetings.domain.support;


import cz.upce.gremis.gremisng.meetings.domain.MeetingType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


import static cz.upce.gremis.gremisng.meetings.domain.MeetingType.EXCEPTIONAL;
import static cz.upce.gremis.gremisng.meetings.domain.MeetingType.REGULAR;

@Converter(autoApply = true)
class MeetingTypeConverter implements AttributeConverter<MeetingType, String> {

    @Override
    public String convertToDatabaseColumn(MeetingType attribute) {
        return switch (attribute) {
            case REGULAR -> "R";
            case EXCEPTIONAL -> "E";
        };
    }

    @Override
    public MeetingType convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "R" -> REGULAR;
            case "E" -> EXCEPTIONAL;
            default -> throw new IllegalArgumentException("Unknown meeting type " + dbData);
        };
    }
}
