package com.lkm.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ContactNumberAnnotationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface ContactNumberAnnotation 
{
	String message() default "Contact Number is invalid";
	Class<?> [] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
