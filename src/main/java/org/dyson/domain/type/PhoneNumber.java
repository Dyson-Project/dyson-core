package org.dyson.domain.type;


import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dyson.core.model.ValueObject;

import java.util.Objects;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // Danh cho hibernate
@RequiredArgsConstructor
public class PhoneNumber implements ValueObject {
    public static final String REGEX_PHONE = "^(84|0)[35789][0-9]{8}$";
    private final String value;

    public static PhoneNumber valueOf(String value) {
        return new PhoneNumber(value);
    }

    public static @Nullable PhoneNumber valueOfNullable(@Nullable String value) {
        return Objects.nonNull(value) ? new PhoneNumber(value) : null;
    }
}
