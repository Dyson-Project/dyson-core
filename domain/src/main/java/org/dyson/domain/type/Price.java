package org.dyson.domain.type;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dyson.core.model.ValueObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;


@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // Danh cho hibernate
@RequiredArgsConstructor
public class Price implements ValueObject {
    public static final Price ZERO = new Price(BigDecimal.ZERO);
    private final @NotNull BigDecimal value;

    public static Price valueOf(int price) {
        return new Price(BigDecimal.valueOf(price));
    }

    public static Price valueOf(long price) {
        return new Price(BigDecimal.valueOf(price));
    }

    public static Price valueOf(BigDecimal price) {
        return new Price(price);
    }

    public static BigDecimal validate(BigDecimal price) {
        if (!isValid(price)) {
            throw new IllegalArgumentException("Invalid price: " + price);
        }
        return price;
    }

    public static boolean isValid(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) > 0;
    }

    public static Price add(Price p1, Price p2) {
        return new Price(p1.value.add(p2.value));
    }

    public static Price sum(Price... prices) {
        return Arrays.stream(prices).reduce(Price.ZERO, Price::add);
    }

    public Price multiply(int n) {
        return new Price(this.value.multiply(BigDecimal.valueOf(n)));
    }

    public Price subtract(Price p) {
        return new Price(this.value.subtract(p.value));
    }

    @Override
    public String toString() {
        return value.toEngineeringString();
    }

    @Nullable
    public static Price ofNullable(@Nullable BigDecimal discountValue) {
        return Objects.nonNull(discountValue) ? new Price(discountValue) : null;
    }
}
