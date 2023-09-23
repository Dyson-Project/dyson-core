package org.dyson.domain.type.extractor;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.dyson.domain.type.PhoneNumber;

@UnwrapByDefault
public class PhoneNumberExtractor implements ValueExtractor<@ExtractedValue(type = String.class) PhoneNumber> {
    @Override
    public void extractValues(PhoneNumber originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}
