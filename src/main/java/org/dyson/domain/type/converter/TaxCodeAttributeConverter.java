package org.dyson.domain.type.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.dyson.domain.type.TaxCode;

@Converter(autoApply = true)
public class TaxCodeAttributeConverter implements AttributeConverter<TaxCode, String> {
    @Override
    public String convertToDatabaseColumn(TaxCode attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TaxCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new TaxCode(dbData);
    }
}
