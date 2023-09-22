package org.dyson.domain.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dyson.core.model.ValueObject;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ManualCode implements ValueObject {
    private final String value;

    public ManualCode(String value) {
        this.value = StringUtils.upperCase(value);
    }
}
