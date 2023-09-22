package org.dyson.domain.type.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.dyson.domain.type.ManualCode;

@Converter(autoApply = true)
public class ManualCodeAttributeConverter implements AttributeConverter<ManualCode, String> {
    @Override
    public String convertToDatabaseColumn(ManualCode attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ManualCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new ManualCode(dbData);
    }
}
