package org.dyson.domain.type;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dyson.core.model.ValueObject;

import java.util.Objects;

@Embeddable
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Address implements ValueObject {
    @NotBlank
    private final String provinceCode;
    @NotBlank
    private final String districtCode;
    @NotBlank
    private final String wardCode;
    private final @Nullable String line1;
    private final @Nullable String country;
    private final @Nullable Integer zipCode;

    public Address(String provinceCode, String districtCode, String wardCode) {
        this.provinceCode = provinceCode;
        this.districtCode = districtCode;
        this.wardCode = wardCode;
        this.country = "VN";
        this.zipCode = 1000;
        this.line1 = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(zipCode, address.zipCode) &&
            Objects.equals(districtCode, address.districtCode) &&
            Objects.equals(wardCode, address.wardCode) &&
            Objects.equals(provinceCode, address.provinceCode) &&
            Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wardCode, districtCode, provinceCode, country, zipCode);
    }
}
