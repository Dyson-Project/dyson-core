package org.dyson.domain.type.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.dyson.domain.type.Email;

@Converter(autoApply = true)
public class EmailAttributeConverter implements AttributeConverter<Email, String> {
    @Override
    public String convertToDatabaseColumn(Email attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Email(dbData);
    }
}
