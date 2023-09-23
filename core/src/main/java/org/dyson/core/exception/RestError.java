package org.dyson.core.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.ConstraintViolation;
import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 *
 */
@Data
public class RestError {

    private ZonedDateTime timestamp;
    @JsonIgnore
    private HttpStatusCode httpStatusCode;
    private String exception;
    private String code;
    private String message;
    private String trace;
    private List<RestSubError> subErrors = new ArrayList<>();
    private String debugMessage;

    private RestError() {
        timestamp = ZonedDateTime.now();
    }

    public RestError(HttpStatusCode status, Throwable ex) {
        this(status, ex.getMessage(), ex);
    }

    public RestError(int status, Throwable ex) {
        this(HttpStatusCode.valueOf(status), ex.getMessage(), ex);
    }


    public RestError(HttpStatusCode status, String message, Throwable ex) {
        this();
        this.message = message;
        this.httpStatusCode = status;
        this.exception = ex.getClass().getName();
        this.debugMessage = ex.getLocalizedMessage();
    }

    public RestError(HttpStatusCode status, String code, String message, Throwable ex) {
        this();
        this.code = code;
        this.message = message;
        this.httpStatusCode = status;
        this.exception = ex.getClass().getName();
        this.debugMessage = ex.getLocalizedMessage();
    }

    public int getStatus() {
        return httpStatusCode.value();
    }

    private void addSubError(RestSubError subError) {
        subErrors.add(subError);
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new RestValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new RestValidationError(object, message));
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError(
            fieldError.getObjectName(),
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage());
    }

    /**
     * @param fieldErrors
     */
    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }

    private void addValidationError(ObjectError objectError) {
        this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
    }

    /**
     * @param globalErrors
     */
    public void addValidationError(List<ObjectError> globalErrors) {
        globalErrors.forEach(this::addValidationError);
    }

    /**
     * @param cv
     */
    private void addValidationError(ConstraintViolation<?> cv) {
        this.addValidationError(
            cv.getRootBeanClass().getSimpleName(),
            ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
            cv.getInvalidValue(),
            cv.getMessage());
    }

    public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
        constraintViolations.forEach(this::addValidationError);
    }


}