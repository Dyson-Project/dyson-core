package org.dyson.domain.type.extractor;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.dyson.domain.type.Email;

@UnwrapByDefault
public class EmailExtractor implements ValueExtractor<@ExtractedValue(type = String.class) Email> {
    @Override
    public void extractValues(@ExtractedValue(type = String.class) Email originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}