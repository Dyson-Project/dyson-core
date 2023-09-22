package org.dyson.domain.type.extractor;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.dyson.domain.type.Fullname;

@UnwrapByDefault
public class FullnameExtractor implements ValueExtractor<@ExtractedValue(type = String.class) Fullname> {
    @Override
    public void extractValues(@ExtractedValue(type = String.class) Fullname originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.toString());
    }
}
