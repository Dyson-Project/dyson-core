package org.dyson.core.validator;

import java.util.function.Supplier;


/**
 *
 */
public class Validator {

    /**
     * @param obj
     * @param message
     * @param <T>
     * @return
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        } else {
            return obj;
        }
    }

    /**
     * @param obj
     * @param message
     * @param <T>
     */
    public static <T> void mustBeNull(T obj, String message) {
        if (obj != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @param value
     * @param exceptionSupplier
     * @param <X>
     * @throws X
     */
    public static <X extends Throwable> void require(boolean value, Supplier<X> exceptionSupplier) throws X {
        if (!value) {
            throw exceptionSupplier.get();
        }
    }
}
