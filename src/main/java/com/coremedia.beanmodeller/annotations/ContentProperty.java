package com.coremedia.beanmodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface ContentProperty {
  String PROPERTY_NAME_USE_METHOD_NAME = "[useMethodName]";
  int STRING_PROPERTY_DEFAULT_LENGTH = -1;
  String MARKUP_PROPERTY_DEFAULT_GRAMMAR = "[useDefaultGrammar]";

  String propertyName() default PROPERTY_NAME_USE_METHOD_NAME;

  int stringLength() default STRING_PROPERTY_DEFAULT_LENGTH;

  String propertyXmlGrammar() default MARKUP_PROPERTY_DEFAULT_GRAMMAR;
}
