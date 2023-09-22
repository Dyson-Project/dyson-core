package org.dyson.domain.type.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dyson.domain.type.PhoneNumber;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Pattern(regexp = PhoneNumber.REGEX_PHONE, message = "{phone.number.well.formed.violation}")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidPhoneNumber {

    String message() default "{phone.number.well.formed.violation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
