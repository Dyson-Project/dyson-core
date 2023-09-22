package org.dyson.domain.type;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dyson.core.model.ValueObject;

@Embeddable
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Fullname implements ValueObject {
    private final String firstName;
    private final String lastName;

    public Fullname(@NotBlank String rawName) {
        this.firstName = rawName;
        this.lastName = "";
    }

    public String toString() {
        if (StringUtils.isEmpty(lastName)) {
            return firstName;
        }
        return "%s %s".formatted(firstName, lastName);
    }

}
