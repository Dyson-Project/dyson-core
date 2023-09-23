package org.dyson.domain.type;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dyson.core.model.ValueObject;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // Danh cho hibernate
@RequiredArgsConstructor
public class TaxCode implements ValueObject {
    @NotBlank
    private final String value;
}
