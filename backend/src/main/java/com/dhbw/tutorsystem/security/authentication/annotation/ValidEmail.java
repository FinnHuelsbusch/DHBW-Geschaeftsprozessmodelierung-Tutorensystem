package com.dhbw.tutorsystem.security.authentication.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import com.dhbw.tutorsystem.user.User;

// Custom annotation for validating a given email 
// must give custom message to prevent regex being exposed!
@Pattern(regexp = User.validEmailRegex, message = "Invalid e-mail format")
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface ValidEmail {
    // message is overwritten above
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
