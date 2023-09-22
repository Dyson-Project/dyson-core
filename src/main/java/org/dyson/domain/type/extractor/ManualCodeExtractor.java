package org.dyson.domain.type.extractor;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.dyson.domain.type.ManualCode;

@UnwrapByDefault
public class ManualCodeExtractor implements ValueExtractor<@ExtractedValue(type = String.class) ManualCode> {
    @Override
    public void extractValues(@ExtractedValue(type = String.class) ManualCode originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}
