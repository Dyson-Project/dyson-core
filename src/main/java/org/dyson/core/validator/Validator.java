package org.dyson.core.validator;

import java.util.function.Supplier;

public class Validator {

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        } else {
            return obj;
        }
    }

    public static <T> void mustBeNull(T obj, String message) {
        if (obj != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <X extends Throwable> void require(boolean value, Supplier<X> exceptionSupplier) throws X {
        if (!value) {
            throw exceptionSupplier.get();
        }
    }
}
