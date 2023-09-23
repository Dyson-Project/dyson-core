package org.dyson.core.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class RestValidationError extends RestSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public RestValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }

    public RestValidationError(String object, String field, Object rejectedValue, String message) {
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }
}