package org.dyson.domain.type.extractor;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.dyson.domain.type.TaxCode;

@UnwrapByDefault
public class TaxCodeExtractor implements ValueExtractor<@ExtractedValue(type = String.class) TaxCode> {
    @Override
    public void extractValues(TaxCode originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}
