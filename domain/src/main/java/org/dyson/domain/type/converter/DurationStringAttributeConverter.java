package org.dyson.domain.type.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
public class DurationStringAttributeConverter implements AttributeConverter<Duration, String> {
    @Override
    public String convertToDatabaseColumn(Duration attribute) {
        return attribute.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        return Duration.parse(dbData);
    }
}
