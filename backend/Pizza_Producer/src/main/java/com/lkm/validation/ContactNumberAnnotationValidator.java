package com.lkm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactNumberAnnotationValidator implements ConstraintValidator<ContactNumberAnnotation, String> {


	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value.length()!=10)
			return false;
		else
			return true;
	}
}
