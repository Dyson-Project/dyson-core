package org.dyson.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RestValidationError extends RestSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    RestValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}